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
package be.belgium.eid.objects;

/**
 * The IDPIN class contains the information of a PIN, there are 1 or more on the
 * beID card.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 13 Dec 2007
 */
public class IDPIN {

	/** Contains the type of PIN on the card */
	private final PINType fPINType;

	/** Contains the reference or ID of the PIN */
	private final char fID;

	/** Contains the usage code of the PIN */
	private final PINUsageCode fUsageCode;

	/** Contains the flags of the current PIN */
	private final int fFlags;

	/**
	 * Contains the label that comes with the PIN to explain more about it's
	 * type
	 */
	private final String fLabel;

	/**
	 * Creates a new PIN object from a smart card, initializing it with the
	 * given data.
	 * 
	 * @param pinType
	 *            is the type of PIN on the card
	 * @param id
	 *            is the reference or ID of the PIN
	 * @param usageCode
	 *            is the usage code for the PIN
	 * @param flags
	 *            are the flags for the current PIN
	 * @param label
	 *            is the label that comes with the PIN to explain more about
	 *            it's type
	 */
	public IDPIN(final PINType pinType, final char id,
			final PINUsageCode usageCode, final int flags, final String label) {
		fPINType = pinType;
		fID = id;
		fUsageCode = usageCode;
		fFlags = flags;
		fLabel = label;
	}

	/**
	 * Returns the type of PIN on the card.
	 * 
	 * @return the type of PIN
	 */
	public PINType getPINType() {
		return fPINType;
	}

	/**
	 * Returns the reference or ID of the PIN.
	 * 
	 * @return the reference or ID
	 */
	public char getID() {
		return fID;
	}

	/**
	 * Returns the usage code for the PIN.
	 * 
	 * @return the usageCode
	 */
	public PINUsageCode getUsageCode() {
		return fUsageCode;
	}

	/**
	 * Returns the flags of the current PIN.
	 * 
	 * @return the flags
	 */
	public int getFlags() {
		return fFlags;
	}

	/**
	 * Returns the label that comes with the PIN to explain more about it's
	 * type.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return fLabel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ID/reference: " +  (int) fID + "\n" + 
			"PIN type: " + fPINType + "\n" + 
			"Usage code: " + fUsageCode + "\n" + 
			"Flags: " + fFlags + "\n" + 
			"Label: " + fLabel + "\n";
	}
}
