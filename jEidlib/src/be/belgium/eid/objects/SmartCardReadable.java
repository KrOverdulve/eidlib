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
package be.belgium.eid.objects;

/**
 * This interface can be used by the implementing class to define an object
 * that can be read from a smart card. It defines certain constants that are
 * useful for performing operations on a smart card.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 07 Dec 2007
 * 
 */
public interface SmartCardReadable {

	/**
	 * Contains the attribute that indicates that we are reading from a file on
	 * the smart card
	 */
	byte[] fgMF = { (byte) 0x3F, (byte) 0x00 };
	byte[] fgDFID = { (byte) 0xDF, (byte) 0x01 };
	byte[] fgDFCert = { (byte) 0xDF, (byte) 0x00 };

	/**
	 * Contains the different kinds of data tags to indicate the type of data to
	 * read
	 */
	byte fgDataTag = (byte) 0x40;
	byte fgCertTag = (byte) 0x50;

	byte fgDataTagID = (byte) 0x31;
	byte fgDataTagIDSIG = (byte) 0x32;
	byte fgDataTagADDR = (byte) 0x33;
	byte fgDataTagADDRSIG = (byte) 0x34;
	byte fgDataTagPHOTO = (byte) 0x35;

	char fgDataTagTOKENINFO = (byte) 0x32;
	byte fgDataTagRN = (byte) 0x3C; 
	byte fgDataTagCA = (byte) 0x3A;
	byte fgDataTagROOT = (byte) 0x3B;
	byte fgDataTagAUTH = (byte) 0x38;
	byte fgDataTagSIG = (byte) 0x39;

	/**
	 * Contains the maximum sizes that different objects can have
	 */
	int fgMAX_SIGNATURE_LEN = 256;
}
