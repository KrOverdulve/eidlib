/*
 * 5. LICENSE ISSUES 
 * The eID Toolkit uses several third-party libraries or code. 
 * Redistributions in any form of the eID Toolkit – even embedded in a compiled application – 
 * must reproduce all the eID Toolkit and third-party’s copyright notices, list of conditions, 
 * disclaimers, and any other materials provided with the distribution. 
 * 
 * 5.1 Disclaimer 
 * This eID Toolkit is provided by the Belgian Government “as is”, and any expressed or implied 
 * warranties, including, but not limited to, the implied warranties of merchantability and fitness 
 * for a particular purpose are disclaimed.  In no event shall the Belgian Government or its 
 * contributors be liable for any direct, indirect, incidental, special, exemplary, or consequential 
 * damages (including, but not limited to, procurement of substitute goods or services; loss of use, 
 * data, or profits; or business interruption) however caused and on any theory of liability, whether 
 * in contract, strict liability, or tort (including negligence or otherwise) arising in any way out of 
 * the use of this Toolkit, even if advised of the possibility of such damage. 
 * However, the Belgian Government will ensure the maintenance of the Toolkit – that is, bug 
 * fixing, and support of new versions of the Electronic Identity card.
 * 
 * Source: DeveloperGuide.pdf
 */
package be.belgium.eid.samples;

import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.exceptions.EIDException;

/**
 * The Information retrieval sample application fetches and prints all the
 * information of the inserted eID card. The eID card ought to be inserted
 * before running this program.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 22 Apr 2007
 */
public class InformationRetrieval {

	/**
	 * Main program that prints all the info.
	 * 
	 * @param args
	 *            no argument need to be given
	 */
	public static void main(String[] args) {
		// Load the eID
		try {
			BeID eID = new BeID(true); // We allow information to be fetched
										// from test cards

			// We fetch the information
			System.out.println("InformationRetrieval -- ID information:");
			System.out.println(eID.getIDData().toString());
			System.out.println("InformationRetrieval -- Address information:");
			System.out.println(eID.getIDAddress().toString());
			System.out
					.println("InformationRetrieval -- Photo is saved to file:");
			eID.getIDPhoto().writeToFile(eID.getIDData().getName());
		} catch (EIDException e) {
			System.err.println("InformationRetrieval eIDException -- "
					+ e.getMessage());
		} catch (Exception e) {
			System.err.println("InformationRetrieval exception -- "
					+ e.getMessage());
		}
	}
}
