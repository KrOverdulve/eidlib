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

import be.belgium.eid.objects.SmartCardReadable;
import be.belgium.eid.exceptions.BufferTooSmallException;

/**
 * The IDPIN class contains the version information of the smart card.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 24 Mar 2007
 */
public class IDVersion implements SmartCardReadable {

	/** Contains the number of characters that chip number can have */
	public static int fgCHIPNUMBERLENGTH = 16;

	/** Contains the chip number */
	private byte[] fChipNumber;

	/** Contains the component code */
	private final byte fComponentCode;

	/** Contains the OS number */
	private final byte fOSNumber;

	/** Contains the OS version */
	private final byte fOSVersion;

	/** Contains the softmask number */
	private final byte fSoftmaskNumber;

	/** Contains the softmask version */
	private final byte fSoftmaskVersion;

	/** Contains the global OS version */
	private final byte fGlobalOSVersion;

	/** Contains the applet version */
	private final byte fAppletVersion;

	/** Contains the applet interface version */
	private final byte fAppletInterfaceVersion;

	/** Contains the PKCS1 support byte */
	private final byte fPKCS1Support;

	/** Contains the key exchange version */
	private final byte fKeyExchangeVersion;

	/** Contains the application life cycle byte */
	private final byte fApplicationLifeCycle;

	/** Contains the token information */
	private final IDTokenInfo fTokenInfo;

	/**
	 * Parses the given characterstreams in version information and token
	 * information to result in a fully initialized IDVersion object.
	 * 
	 * @param characterStreamVersion
	 *            is the character stream to parse to get the version
	 *            information
	 * @param characterStreamToken
	 *            is the character stream to parse to get the token information
	 * @return a fully initialized version information object
	 * 
	 * @throws BufferTooSmallException
	 *             when the buffer of the characters was too small to get valid
	 *             data from
	 */
	public static IDVersion parse(final byte[] characterStreamVersion,
			final byte[] characterStreamToken) throws BufferTooSmallException {
		// Parse chipnumber
		final byte[] chipNumber = new byte[fgCHIPNUMBERLENGTH];
		System.arraycopy(characterStreamVersion, 0, chipNumber, 0,
				fgCHIPNUMBERLENGTH);
		int counter = fgCHIPNUMBERLENGTH;

		// Return version
		return new IDVersion(
				chipNumber,
				characterStreamVersion[counter++],
				characterStreamVersion[counter++],
				characterStreamVersion[counter++],
				characterStreamVersion[counter++],
				characterStreamVersion[counter++],
				characterStreamVersion[counter++],
				characterStreamVersion[++counter],// We make a hop for the
													// Global OS version
				characterStreamVersion[++counter],
				characterStreamVersion[++counter],
				characterStreamVersion[++counter],
				characterStreamVersion[++counter], IDTokenInfo
						.parse(characterStreamToken));
	}

	/**
	 * Initializes the version information with the given information.
	 * 
	 * @param chipNumber
	 *            is the chip number
	 * @param componentCode
	 *            is the component code
	 * @param osNumber
	 *            is the OS number
	 * @param osVersion
	 *            is the OS version
	 * @param softmaskNumber
	 *            is the softmask number
	 * @param softmaskVersion
	 *            is the softmask version
	 * @param appletInterfaceVersion
	 *            is the applet interface version
	 * @param pkcs1Support
	 *            is the PKCS1 support byte
	 * @param keyExchangeVersion
	 *            is the key exchange version
	 * @param applicationLifeCycle
	 *            is the application life cycle byte
	 * @param globalOSVersion
	 *            is the global OS version
	 * @param appletVersion
	 *            is the version of the applet
	 * @param tokenInfo
	 *            is the information about the tokens
	 */
	public IDVersion(final byte[] chipNumber, final byte componentCode,
			final byte osNumber, final byte osVersion,
			final byte softmaskNumber, final byte softmaskVersion,
			final byte appletVersion, final byte globalOSVersion,
			final byte appletInterfaceVersion, final byte pkcs1Support,
			final byte keyExchangeVersion, final byte applicationLifeCycle,
			final IDTokenInfo tokenInfo) {
		fChipNumber = chipNumber.clone();
		fComponentCode = componentCode;
		fOSNumber = osNumber;
		fChipNumber = chipNumber.clone();
		fOSVersion = osVersion;
		fSoftmaskNumber = softmaskNumber;
		fSoftmaskVersion = softmaskVersion;
		fGlobalOSVersion = globalOSVersion;
		fAppletVersion = appletVersion;
		fAppletInterfaceVersion = appletInterfaceVersion;
		fPKCS1Support = pkcs1Support;
		fKeyExchangeVersion = keyExchangeVersion;
		fApplicationLifeCycle = applicationLifeCycle;
		fTokenInfo = tokenInfo;
	}

	/**
	 * Returns the chip number.
	 * 
	 * @return the chip number
	 */
	public byte[] getChipNumber() {
		return fChipNumber.clone();
	}

	/**
	 * Returns the component code.
	 * 
	 * @return the component code
	 */
	public byte getComponentCode() {
		return fComponentCode;
	}

	/**
	 * Returns the OS number.
	 * 
	 * @return the OS number
	 */
	public byte getOSNumber() {
		return fOSNumber;
	}

	/**
	 * Returns the OS version.
	 * 
	 * @return the OS version
	 */
	public byte getOSVersion() {
		return fOSVersion;
	}

	/**
	 * Returns the softmask number.
	 * 
	 * @return the softmask number
	 */
	public byte getSoftmaskNumber() {
		return fSoftmaskNumber;
	}

	/**
	 * Returns the softmask version.
	 * 
	 * @return the softmask version
	 */
	public byte getSoftmaskVersion() {
		return fSoftmaskVersion;
	}

	/**
	 * Returns the applet interface version.
	 * 
	 * @return the applet interface version
	 */
	public byte getAppletInterfaceVersion() {
		return fAppletInterfaceVersion;
	}

	/**
	 * Returns the PKCS1 support byte.
	 * 
	 * @return the PKCS1 support byte
	 */
	public byte getPKCS1Support() {
		return fPKCS1Support;
	}

	/**
	 * Returns the key exchange version.
	 * 
	 * @return the key exchange version
	 */
	public byte getKeyExchangeVersion() {
		return fKeyExchangeVersion;
	}

	/**
	 * Returns the application life cycle byte.
	 * 
	 * @return the application life cycle byte
	 */
	public byte getApplicationLifeCycle() {
		return fApplicationLifeCycle;
	}

	/**
	 * Returns the global OS version.
	 * 
	 * @return the global OS version
	 */
	public byte getGlobalOSVersion() {
		return fGlobalOSVersion;
	}

	/**
	 * Returns the version of the applet.
	 * 
	 * @return the version of the applet
	 */
	public byte getAppletVersion() {
		return fAppletVersion;
	}

	/**
	 * Returns the information about the tokens.
	 * 
	 * @return the information about the tokens
	 */
	public IDTokenInfo getTokenInformation() {
		return fTokenInfo;
	}
}
