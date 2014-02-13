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
 * The "card not found" exception indicates that the card with which the
 * connection is to be made is not present in the card reader. The causes can be
 * that it was just not present, that it was removed since the connection was
 * made before or that the card was reset, it could also be that no connection
 * has yet been made with the smart card reader.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 17 Dec 2007
 */
@SuppressWarnings("serial")
public class CardNotFoundException extends Exception {

	/** Contains the types of card not found exception */
	public static enum CardNotFoundType {
		NOT_CONNECTED, NOT_PRESENT, REMOVED, RESET
	}

	/** Contains type of the current exception */
	private CardNotFoundType fType;

	/**
	 * Initializes the card not found exception with the given type.
	 * 
	 * @param type
	 *            is the type of card not found exception
	 */
	public CardNotFoundException(final CardNotFoundType type) {
		super();
		setType(type);
	}

	/**
	 * Initializes the card not found exception with the given type and the
	 * given cause of the problem.
	 * 
	 * @param cause
	 *            is the cause of the exception
	 * @param type
	 *            is the type of card not found exception
	 */
	public CardNotFoundException(final Throwable cause,
			final CardNotFoundType type) {
		super(cause);
		setType(type);
	}

	/**
	 * Sets the type of card not found exception.
	 * 
	 * @param type
	 *            the type to set
	 */
	private void setType(final CardNotFoundType fType) {
		this.fType = fType;
	}

	/**
	 * Returns the type of card not found exception.
	 * 
	 * @return the type
	 */
	public CardNotFoundType getType() {
		return fType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The card wasn't found in the system (" + fType + ").";
	}
}
