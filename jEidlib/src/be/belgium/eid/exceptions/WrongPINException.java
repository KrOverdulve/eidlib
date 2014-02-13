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
package be.belgium.eid.exceptions;

/**
 * The "wrong PIN" exception indicates that the entered PIN was invalid.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 11 Mar 2007
 */
@SuppressWarnings("serial")
public class WrongPINException extends Exception {

	/** Contains the entered PIN that was invalid */
	private final String fEnteredPIN;

	/** Contains the number of tries left */
	private final int fNoTriesLeft;

	/**
	 * Initializes the exception with the given invalid PIN and the number of
	 * tries that the user can try to enter an invalid PIN before blocking the
	 * card.
	 * 
	 * @param enteredPIN
	 *            is the invalid PIN that was entered
	 * @param noTriesLeft
	 *            is the number of tries that the user can try to enter the PIN
	 */
	public WrongPINException(final String enteredPIN, final int noTriesLeft) {
		super();
		fEnteredPIN = enteredPIN;
		fNoTriesLeft = noTriesLeft;
	}

	/**
	 * Initializes the exception with the given invalid PIN and the number of
	 * tries that the user can try to enter an invalid PIN before blocking the
	 * card and the given cause.
	 * 
	 * @param cause
	 *            is the cause of the exception
	 * @param enteredPIN
	 *            is the invalid PIN that was entered
	 * @param noTriesLeft
	 *            is the number of tries that the user can try to enter the PIN
	 */
	public WrongPINException(final Throwable cause, final String enteredPIN,
			final int noTriesLeft) {
		super(cause);
		fEnteredPIN = enteredPIN;
		fNoTriesLeft = noTriesLeft;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The given PIN " + fEnteredPIN + " is not valid. You have "
				+ fNoTriesLeft
				+ " to remaining tries left enter the valid PIN code.";
	}

	/**
	 * Returns the invalid entered PIN code.
	 * 
	 * @return the invalid PIN
	 */
	public String getInvalidEnteredPIN() {
		return fEnteredPIN;
	}

	/**
	 * Returns the number of tries left that the user can try to enter the PIN
	 * code without blocking the card.
	 * 
	 * @return the number of tries left
	 */
	public int getNumberOfTriesLeft() {
		return fNoTriesLeft;
	}

}
