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
package be.belgium.eid.event;

import javax.smartcardio.CardTerminal;

import be.belgium.eid.eidlib.SmartCard;

/**
 * This class prompts the card reader to check whether the card is alive. With
 * 'alive' we mean that the card is inserted in the card reader.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 11 Feb 2008
 */
public class CardAlivePromptTask extends Thread {
	
	/** Lock time to wait for card removal */
	public static long fsWaitCardAbsent = 500;

	/** Lock time to wait for card insertion */
	public static long fsWaitCardPresent = 500;

	/**
	 * Contains the card reader to use to prompt whether the card is in the
	 * smart card reader
	 */
	private final SmartCard fCard;

	/** Contains the card listener */
	private final CardListener fCardListener;
	
	/** Indicates that the thread needs to continue execution */
	private boolean fExecute = true;

	/**
	 * Initializes the task with the given card reader. This latter is used to
	 * check whether the card is still alive. When events occur, the given
	 * listener is notified.
	 * 
	 * @param cr
	 *            is the card reader to use to prompt whether the card is in the
	 *            card reader
	 * @param cl
	 *            is the card listener to use to listen to changes
	 */
	public CardAlivePromptTask(final SmartCard cr, final CardListener cl) {
		super();

		fCard = cr;
		fCardListener = cl;
	}

	/**
	 * Repeatably checks whether the card is currently inserted in the card
	 * reader and performs actions when the context has been changed.
	 */
	public void run() {
		while (fExecute) {
			try {
				if (!fCard.isConnected()) {
					// Wait for a card to be present on each terminal
					for (CardTerminal ct : SmartCard.getSmartCardReaders()) {
						if (ct.waitForCardPresent(fsWaitCardPresent)) {
							fCard.connectCard(ct.getName());
							fCardListener.cardInserted();
							break;
						}
					}	
				} else {
					if (fCard.getConnectedReader().waitForCardAbsent(fsWaitCardAbsent)) {
						fCard.disconnect();
						fCardListener.cardRemoved();
					}
				}
			} catch (Exception e) {
				// TODO Ignore
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Indicates that the thread needs to stop running.
	 */
	public void stopThread() {
		fExecute = false;
	}
}
