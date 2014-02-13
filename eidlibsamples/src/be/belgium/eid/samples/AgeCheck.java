/*
 * 5. LICENSE ISSUES 
 * The eID Toolkit uses several third-party libraries or code. 
 * Redistributions in any form of the eID Toolkit � even embedded in a compiled application � 
 * must reproduce all the eID Toolkit and third-party�s copyright notices, list of conditions, 
 * disclaimers, and any other materials provided with the distribution. 
 * 
 * 5.1 Disclaimer 
 * This eID Toolkit is provided by the Belgian Government �as is�, and any expressed or implied 
 * warranties, including, but not limited to, the implied warranties of merchantability and fitness 
 * for a particular purpose are disclaimed.  In no event shall the Belgian Government or its 
 * contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential 
 * damages (including, but not limited to, procurement of substitute goods or services; loss of use, 
 * data, or profits; or business interruption) however caused and on any theory of liability, whether 
 * in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of 
 * the use of this Toolkit, even if advised of the possibility of such damage. 
 * However, the Belgian Government will ensure the maintenance of the Toolkit � that is, bug 
 * fixing, and support of new versions of the Electronic Identity card.
 * 
 * Source: DeveloperGuide.pdf
 */
package be.belgium.eid.samples;

import java.util.Date;
import java.util.Scanner;

import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.event.CardAdapter;
import be.belgium.eid.exceptions.EIDException;

/**
 * The Age checker sample application allows a user to verify it's age that needs
 * to exceed a certain value. For example when going to a Movies complex, there
 * is a need for verification  of the age of a certain person that wishes to see
 * an adult movie. Other means could be for buying alcohol, cigarettes, violent video 
 * games, etc... .
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 21 Apr 2007
 */
public class AgeCheck {
	
	/**
	 * Main program that checks the age.
	 * 
	 * @param args must contain one positive integral argument that contains the age to check
	 */
	public static void main(String[] args) {
		// The first and only argument that the program should receive is the age to verify
		if (args.length != 1) {
			System.err.println("AgeChecker -- Invalid number of arguments.");
		} else {
			try {
				final Long ageToCheck = Long.parseLong(args[0]);	// Age to verify
				if (ageToCheck < 0) {
					System.err.println("AgeChecker -- Given argument should be a positive value.");
				} else {	
					// Load the eID 
					final BeID eID = new BeID(false);	// We don't allow test cards to verify their age
					eID.enableCardListener(new CardAdapter() {
						public void cardInserted() {
							// We now check the age
							try {
								// Fetch birth date inserted card
								Date birthdate = eID.getIDData().getBirthDate();

								// Calculate what the upperbound of the birthdate is
								// (We ignore Leap Years)
								long timeBeforeNow = ageToCheck * 365 /* DAYS A YEAR */ 
										* 24 /* HOURS A DAY */ * 60 /* MINUTES PER HOUR */
										* 60 /* SEC/MINUTE */ * 1000;
								Date upperbound = new Date();
								upperbound.setTime(upperbound.getTime() - timeBeforeNow);
								
								// Check age
								if (birthdate.after(upperbound)) {
									System.out.println("AgeChecker -- Too young.");
								} else {
									System.out.println("AgeChecker -- OK.");
								}
							} catch (EIDException e) {
								e.printStackTrace(); 
								System.err.println("AgeChecker eIDException: -- " + e.getMessage());
							} catch (Exception e) {
								System.err.println("AgeChecker exception: -- " + e.getMessage());
							}
							
						}
					});
					
					// Keep on checking ages until QUIT is typed or process is ended
					System.out.println("AgeChecker -- type QUIT to end program.");
					String input = "";
					while (!input.equalsIgnoreCase("QUIT")) {
						input = new Scanner(System.in).next();
					}
				}
			} catch (NumberFormatException e) {
				System.err.println("AgeChecker -- Given argument should be an integral value.");
			}
		}
	}
}
