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

import be.belgium.eid.eidcommon.ByteConverter;
import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.exceptions.EIDException;
import be.belgium.eid.security.CertificateChain;
import be.belgium.eid.security.RNCertificate;

/**
 * In our daily computer activities we need passwords all the time. Although it
 * is not advised to use the same password over and over, most of the users do
 * this anyway. Another bad habit is to have a file on your PC that contains all
 * the passwords you use. <br/> A way to circumvent these disavantages, is to
 * use challenges. With the eID card you can generate challenges, which are
 * random sequences of bytes, based on the information on your smart card (which
 * is more unique than most random number generators). <br/> Although these
 * challenges are random, the eID can generate seemly random challenge responses
 * that is fixes for each challenge. For each generated challenge, the response
 * is always the same for that challenge.
 * <p>
 * 
 * So what our plan is, is to generate a few challenges first (one per website
 * for example) and to write these down or save these to a file or anything.
 * When having generated a challenge, the challenge and challenge response is
 * printed in a hexidecimal format so that we can read it without having
 * non-printable characters cluttering the output. Then we enter the challenge
 * response as password to our website (after small modifications if this is
 * necessary). When we need to enter the password to the website later on, we
 * enter the challenge as an argument to a program and generate the challenge
 * response, now the password is again visible. The main idea for this method
 * is of course that you don't give your eID card to other people and to keep
 * it safe with you.
 * <p>
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 22 Apr 2007
 */
public class SecurityChallenge {

	/**
	 * Runs the security challenge program. NOTE: when no arguments are given, a
	 * challenge and response are generated. When arguments are given (the
	 * challenges), the corresponding challenge responses are generated.
	 * 
	 * @param args are the (optional) arguments to the program
	 */
	public static void main(String[] args) {
		// Load the eID
		try {
			BeID eID = new BeID(false); // We don't allow test cards since they
										// could generate equal responses for 
										// different people and thus defeat security
			
			// First we need to pay attention to forgery so we check the validity
			CertificateChain certChain = eID.getCertificateChain();
			RNCertificate rnCert = eID.getNationalRegisterCertificate();
			System.out.println("Checking certificate validity... ");
			if (eID.verifyOCSP(certChain) && eID.verifyCRL(certChain, rnCert)) {
				System.out.println("SecurityChallenge -- certificates validated OK.");
				
				// Parse the arguments and process
				if (args.length == 0) {
					// Generate challenge and response when no arguments are given
					byte[] challenge = eID.getChallenge();
					byte[] challengeResponse = eID.getChallengeResponse(challenge);
	
					// Print challenge and response
					System.out.println("Challenge: "
							+ ByteConverter.hexify(challenge));
					System.out.println("Challenge response: "
							+ ByteConverter.hexify(challengeResponse));
				} else {
					// For each given challenge, generate response and print
					// information
					for (int i = 0; i < args.length; i++) {
						byte[] challenge = ByteConverter.unhexify(args[i]);
						byte[] challengeResponse = eID
								.getChallengeResponse(challenge);
	
						System.out.println("For challenge " + args[i]
								+ ", the response is: "
								+ ByteConverter.hexify(challengeResponse));
	
					}
				}
			} else {
				System.out.println("SecurityChallenge -- certificates were invalid or revoked.");
			}
		} catch (EIDException e) {
			System.err.println("SecurityChallenge eIDException -- "
					+ e.getMessage());
		} catch (Exception e) {
			System.err.println("SecurityChallenge exception -- "
					+ e.getMessage());
		}
	}

}
