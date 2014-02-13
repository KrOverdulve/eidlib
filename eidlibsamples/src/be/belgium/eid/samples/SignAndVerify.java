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
import be.belgium.eid.eidlib.BeID.SignatureType;
import be.belgium.eid.exceptions.EIDException;

/**
 * The Sign and verify sample application signs a certain text and then verifies
 * it against it's generated signature.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 2 September 2008
 */
public class SignAndVerify {

	/**
	 * Main program that signs and verifies the data
	 * 
	 * @param args
	 *            1 argument needs to be given: the PIN code. This is needed to
	 *            generate the signature
	 */
	public static void main(String[] args) {
		// The first and only argument that the program should receive is the
		// age to verify
		if (args.length != 1) {
			System.err.println("SignAndVerify -- Invalid number of arguments.");
		} else {
			final String textToSign = "And still no Java 6 for Mac OS X...";

			// Load the eID
			try {
				final BeID eID = new BeID(false); // We don't allow test cards
				byte[] signature = eID.generateSignature(textToSign.getBytes(),
						args[0], SignatureType.AUTHENTICATIONSIG);
				System.out.println("Signature: "
						+ ByteConverter.hexify(signature));
				System.out.println("Verification succeeded: "
						+ eID.verifySignature(textToSign.getBytes(), signature,
								SignatureType.AUTHENTICATIONSIG));
			} catch (EIDException e) {
				System.err.println("SignAndVerify -- EIDException: "
						+ e.getMessage());
			}
		}
	}
}
