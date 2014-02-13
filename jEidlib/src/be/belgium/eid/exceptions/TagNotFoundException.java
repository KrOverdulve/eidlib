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
package be.belgium.eid.exceptions;

/**
 * The "tag not found" exception indicates that in a TLV mapping the given tag
 * doesn't exist and as such cannot be used to identify data associated with it.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
@SuppressWarnings("serial")
public class TagNotFoundException extends Exception {

	/** contains the tag that wasn't found */
	private final byte fTag;

	/**
	 * Initializes a new tag not found exception.
	 * 
	 * @param tag
	 *            is the tag that wasn't found
	 */
	public TagNotFoundException(final byte tag) {
		super();
		fTag = tag;
	}

	/**
	 * Initializes the tag not found exception with the given tag that caused
	 * the exception and cause of the problem.
	 * 
	 * @param tag
	 *            is the tag that wasn't found
	 * @param cause
	 *            is the cause of the exception
	 */
	public TagNotFoundException(final byte tag, final Throwable cause) {
		super(cause);
		fTag = tag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Throwable#getMessage()
	 */
	public String getMessage() {
		return "The given tag: " + fTag + " was not found in the TLV elements.";
	}
}
