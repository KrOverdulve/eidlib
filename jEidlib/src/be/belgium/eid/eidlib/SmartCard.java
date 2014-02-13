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
package be.belgium.eid.eidlib;

import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

import be.belgium.eid.exceptions.CardNotFoundException;
import be.belgium.eid.exceptions.InvalidSWException;
import be.belgium.eid.exceptions.NoReadersFoundException;
import be.belgium.eid.exceptions.WrongPINException;
import be.belgium.eid.objects.SmartCardReadable;

/**
 * The SmartCard class contains the different operations that can be performed
 * on a regular (not specifically e-ID related) smart card. It contains more
 * general operations that are more difficult to work with than those of
 * {@link be.belgium.eid.eidlib.BeID}. While the BeID class connects to the
 * smart card implicitly when performing an operation, this class does not do
 * this. Before doing anything, the method
 * {@link be.belgium.eid.eidlib.SmartCard#connectCard()} should be executed.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 07 Dec 2007
 */
public class SmartCard {

	/** Contains the card on which the operations need to be performed */
	private Card fCard;

	/** Contains the smart card reader with which a connection has been made */
	private CardTerminal fTerminal;

	/** Contains the channel over which the communication occurs */
	private CardChannel fChannel;

	/** Contains the ATR (Answer To Reset) of the card */
	private ATR fATR;

	/** Indicates whether a successful connection has already been made */
	private boolean fIsConnected = false;

	/**
	 * Returns the smart card readers in the current context of the system.
	 * 
	 * @return a list of smart card readers currently connected to the system
	 * @throws CardException
	 *             if the card operation failed
	 */
	public static List<CardTerminal> getSmartCardReaders() throws CardException {
		return TerminalFactory.getDefault().terminals().list();
	}

	/**
	 * Sets up the system. Note that you have to call <i>connect()</i> or
	 * <i>connect(name)</i> before being able to perform any operation or
	 * retrieve any data from the smart card.
	 */
	public SmartCard() {
		super();
		fIsConnected = false;
	}

	/**
	 * Connects the system to the smart card reader with the given name to allow
	 * operations to be performed on the smart card. If the card was already
	 * connected to the system, nothing will be done.
	 * 
	 * @param name
	 *            is the name of the smart card reader to connect with
	 * @throws NoReadersFoundException
	 *             when no card readers were found on the current device
	 * @throws CardException
	 *             if the card operation failed
	 */
	public void connectCard(final String name) throws NoReadersFoundException,
			CardException {
		if (!fIsConnected) {
			// Connects with the smart card reader whose name matches the given
			// name
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();

			// Check all terminals
			for (CardTerminal ct : terminals) {
				// Connect if matched
				if (ct.getName().equals(name)) {
					fTerminal = ct;
					fCard = fTerminal.connect("*");
					fATR = fCard.getATR();
					fChannel = fCard.getBasicChannel();
					fIsConnected = true;
					break;
				}
			}

			// No reader with that name found so throw exception
			if (!fIsConnected) {
				throw new NoReadersFoundException();
			}
		}
	}

	/**
	 * Connects the system to the first compatible smart card reader to allow
	 * operations to be performed on the smart card. If the card was already
	 * connected to the system nothing will be done.
	 * 
	 * @throws NoReadersFoundException
	 *             when no card readers were found on the current device
	 * @throws CardException
	 *             if the card operation failed
	 */
	public void connectCard() throws CardException, NoReadersFoundException {
		if (!fIsConnected) {
			// Connects with the first available smart card reader
			TerminalFactory factory = TerminalFactory.getDefault();
			List<CardTerminal> terminals = factory.terminals().list();

			if (terminals.size() > 0) {
				fTerminal = terminals.get(0);
				fCard = fTerminal.connect("*");
				fATR = fCard.getATR();
				fChannel = fCard.getBasicChannel();
				fIsConnected = true;
			} else {
				throw new NoReadersFoundException();
			}
		}
	}

	/**
	 * Disconnects the card from the connected slot.
	 * 
	 * @throws CardException
	 *             if the card operation failed
	 */
	public void disconnect() throws CardException {
		if (fIsConnected) {
			fCard.disconnect(true);
			fIsConnected = false;
		}
	}

	/**
	 * Indicates whether the smart card is currently connected to the system.
	 * 
	 * @return whether it is already connected
	 */
	public boolean isConnected() {
		return fIsConnected;
	}

	/**
	 * Returns the smart card reader with which the system is currently
	 * connected.
	 * 
	 * @return the name of the currently connected reader
	 * @throws CardNotFoundException
	 *             when the card wasn't found in the system or that no
	 *             connection has yet been made
	 */
	public CardTerminal getConnectedReader() throws CardNotFoundException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		return fTerminal;
	}

	/**
	 * Returns the ATR (Answer To Reset) of the card.
	 * 
	 * @return the ATR
	 * @throws CardException
	 *             if the card operation failed
	 */
	public ATR getATR() throws CardNotFoundException {
		if (isConnected()) {
			return fATR;
		} else {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}
	}

	/**
	 * Locks the connected smart card reader to avoid concurrency problems.
	 * 
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public void beginTransaction() throws CardNotFoundException, CardException {
		synchronized (this) {
			// Handle the case when no connection has yet been made
			if (!isConnected()) {
				throw new CardNotFoundException(
						CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
			} else {
				fCard.beginExclusive();
			}
		}
	}

	/**
	 * Unlocks the connected smart card reader to allow other instances to
	 * access the data on the smart card.
	 * 
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public void endTransaction() throws CardNotFoundException, CardException {
		synchronized (this) {
			// Handle the case when no connection has yet been made
			if (!isConnected()) {
				throw new CardNotFoundException(
						CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
			} else {
				fCard.endExclusive();
			}
		}
	}

	/**
	 * Transmits the given command APDU to the smart card.
	 * 
	 * @param cAPDU
	 *            is the command APDU to send
	 * @return the response as issued by the smart card in a response APDU
	 *         command
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public ResponseAPDU transmitAPDU(final CommandAPDU cAPDU)
			throws CardException, CardNotFoundException {
		if (isConnected()) {
			// Transmit APDU over channel and return response
			return fChannel.transmit(cAPDU);
		} else {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}
	}

	/**
	 * Selects a file on the smart card so that it can be read from or written
	 * to.
	 * 
	 * @param fileID
	 *            is the ID of the file to select
	 * @return the response APDU containing the two resulting status bits
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public ResponseAPDU selectFile(final byte[] fileID)
			throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		return transmitAPDU(new CommandAPDU(0x00, 0xA4, 0x08, 0x0C, fileID,
				0x00));
	}

	/**
	 * Sends a command to the smart card to read a block of the currently
	 * selected file.
	 * 
	 * @param p1
	 *            contains the first parameter to indicate what to read
	 * @param p2
	 *            contains the second parameter to indicate what to read
	 * @param noBytesToRead
	 *            contains the number of bytes to read
	 * @return the block of data read
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public ResponseAPDU readBinaryData(int p1, int p2, int noBytesToRead)
			throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		return transmitAPDU(new CommandAPDU(0x00, 0xB0, p1, p2, noBytesToRead));
	}

	/**
	 * Reads a file from the smart card and returns the contents of the read
	 * file.
	 * 
	 * @param fileID
	 *            is the identifier for the file to read
	 * @param maxOutputLength
	 *            is the maximum length of the output returned by the reader
	 * @return the contents of the file
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public byte[] readFile(byte[] fileID, final int maxOutputLength)
			throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		// Lock card
		this.beginTransaction();

		// Init
		int blocklength = 0xF8;
		int length = 0;
		boolean enough = false;
		byte[] tmpReadData = new byte[maxOutputLength];

		// Select the file to read
		byte[] fullfileID = new byte[2 + fileID.length];
		fullfileID[0] = SmartCardReadable.fgMF[0];
		fullfileID[1] = SmartCardReadable.fgMF[1];
		System.arraycopy(fileID, 0, fullfileID, 2, fileID.length);
		this.selectFile(fullfileID);

		// Keep on reading the file until everything has been read
		while (!enough) {
			// Read block
			int p1 = length / 256;
			int p2 = length % 256;
			int noBytesToRead = blocklength;
			ResponseAPDU rAPDU = readBinaryData(p1, p2, noBytesToRead);

			if ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00)) {
				// Data remains to be read
				System.arraycopy(rAPDU.getData(), 0, tmpReadData, length, rAPDU
						.getData().length);
				length += rAPDU.getData().length;
			} else {
				enough = true;

				if (rAPDU.getSW1() == 0x6C) {
					// Wrong length read (too much), so we are at the end of
					// the file and thus only need to read the value of SW2
					// number of bytes
					blocklength = rAPDU.getSW2();

					p1 = length / 256;
					p2 = length % 256;
					noBytesToRead = blocklength;
					rAPDU = readBinaryData(p1, p2, noBytesToRead);

					// This should have worked
					assert ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00));

					// Copy last bit of data in certificate
					System.arraycopy(rAPDU.getData(), 0, tmpReadData, length,
							blocklength);
					length += blocklength;
				}
			}
		}

		// Unlock card
		this.endTransaction();

		// Return the data
		byte[] result = new byte[length];
		System.arraycopy(tmpReadData, 0, result, 0, length);

		// TODO Avoid an error to occur because transmission is reassured to be
		// complete. Kind of stupid actually but it is not my fault.
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// Ignore
		}
		
		return result;
	}

	/**
	 * Returns the card specific data that also contains version information.
	 * Even when the card has been removed from the smart card reader and the
	 * given parameter is false, the card data is returned from when the
	 * connection was made.
	 * 
	 * @return the card specific data
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public byte[] getCardData() throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		// final CommandAPDU cAPDU = new CommandAPDU((char) 0x80, (char) 0xE4,
		// (char) 0x02, (char) 0x00, (char) 0x9C);
		final ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x80,
				0xE4, 0x00, 0x00, 0x1C));
		return rAPDU.getData();
	}

	/**
	 * Returns the challenge for use in a security related procedure such as an
	 * external authentication command. This challenge can, for example, be a
	 * random number. The challenge-response authentication is a family of
	 * protocols in which one party presents the question and another party must
	 * provide a valid answer to be authenticated. The easiest example of this
	 * protocol is password authentication in which the challenge is the prompt
	 * for the password and the response is the right password.
	 * 
	 * @return the generated challenge
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public byte[] getChallenge() throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		final int MAX_CHALLENGE_LENGTH = 20;
		final ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00,
				0x84, 0x00, 0x00, MAX_CHALLENGE_LENGTH));

		return rAPDU.getData();
	}

	/**
	 * Returns the challenge response for the given generated challenge for use
	 * in a security related procedure such as an external authentication
	 * command. For more information about the challenge itself see
	 * {@link be.belgium.eid.eidlib.SmartCard#getChallenge()}.
	 * 
	 * @param challenge
	 *            is the challenge that was previously generated and for which
	 *            to generate a response
	 * @return the response for the given generated challenge
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 */
	public byte[] getChallengeResponse(final byte[] challenge)
			throws CardNotFoundException, CardException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		final int BEID_MAX_RESPONSE_LEN = 128;

		// Get response for challenge
		byte[] dataToSend = new byte[challenge.length + 2];
		dataToSend[0] = (byte) 0x94;
		dataToSend[1] = (byte) challenge.length;

		// Efficient way to copy two arrays, somewhat like memcpy() in C
		System.arraycopy(challenge, 0, dataToSend, 2, challenge.length);

		final ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00,
				0x88, 0x02, 0x81, dataToSend, BEID_MAX_RESPONSE_LEN));

		return rAPDU.getData();
	}

	/**
	 * Verifies the given PIN code of the PIN with the given reference. Returns
	 * the number of times left that the user can attempt to verify the code of
	 * the given PIN. Be very careful when using this operation in any kind of
	 * loop to avoid any accidental repetition of the input of an invalid PIN. A
	 * good idea is to break out of this loop when the number of tries left is
	 * 1.
	 * 
	 * @param code
	 *            is the PIN code with which to verify the operation
	 * @return the number of tries left
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 * @throws WrongPINException
	 *             when the wrong PIN was entered
	 * @throws InvalidSWException
	 *             when the status words returned were not expected
	 */
	public int verifyPIN(String code) throws CardNotFoundException,
			CardException, WrongPINException, InvalidSWException {
		// Code should have even number of characters
		String evenLengthCode = code;
		if (2 * ((int) (evenLengthCode.length() / 2)) != evenLengthCode
				.length()) {
			evenLengthCode = evenLengthCode + "F";
		}

		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		// Write protect the verification of the PIN
		this.beginTransaction();

		// Insert PIN in APDU field
		byte[] pin = new byte[] { (byte) 0x2F, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		pin[0] = (byte) (2 * 16 + evenLengthCode.length());
		for (int i = 0; i < evenLengthCode.length(); i += 2) {
			pin[(i / 2) + 1] = (byte) (Integer.parseInt(evenLengthCode
					.substring(i, i + 2), 16));
		}

		// Send command
		ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00, 0x20,
				0x00, 0x01 /* hardcoded reference */, pin));

		// End lock
		this.endTransaction();

		// Check whether correct
		if ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00)) {
			// Correct PIN code
			return 3;
		} else if (rAPDU.getSW1() == 0x63) {
			// See how many attempts are remaining
			throw new WrongPINException(evenLengthCode, (rAPDU.getSW2() % 16));
		} else {
			throw new InvalidSWException(rAPDU.getSW1(), rAPDU.getSW2());
		}
	}

	/**
	 * Changes the PIN code of the PIN. The (given) old code will be changed to
	 * the given new code. The function returns the number of times left that
	 * the user can attempt to verify the code of the given PIN. Be very careful
	 * when using this operation in any kind of loop to avoid any accidental
	 * repetition of the input of an invalid PIN. A good idea is to break out of
	 * this loop when the number of tries left is 1.
	 * 
	 * @param oldCode
	 *            is the former PIN code to change
	 * @param newCode
	 *            is the new PIN code to change to
	 * @return the number of tries left
	 * @throws CardNotFoundException
	 *             indicates that the card wasn't present in the system or was
	 *             reset, it could also be that no connection with the smart
	 *             card has been made yet
	 * @throws CardException
	 *             if the card operation failed
	 * @throws WrongPINException
	 *             when the wrong PIN was entered
	 * @throws InvalidSWException
	 *             when the status words returned were not expected
	 */
	public int changePIN(String oldCode, String newCode)
			throws CardNotFoundException, CardException, WrongPINException,
			InvalidSWException {
		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		// Codes should have even number of characters
		String evenLengthOldCode = oldCode;
		String evenLengthNewCode = newCode;
		if (2 * ((int) (evenLengthOldCode.length() / 2)) != evenLengthOldCode
				.length()) {
			evenLengthOldCode = evenLengthOldCode + "F";
		}
		if (2 * ((int) (evenLengthNewCode.length() / 2)) != evenLengthNewCode
				.length()) {
			evenLengthNewCode = evenLengthNewCode + "F";
		}

		// Write protect the verification of the PIN
		this.beginTransaction();

		// Insert two PINs in APDU field, one for old PIN and one for new one
		final int PIN_LENGTH = 8;
		byte[] pin = new byte[] { (byte) 0x2F, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0x2F, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, };

		// Insert first PIN
		pin[0] = (byte) (2 * 16 + evenLengthOldCode.length());
		for (int i = 0; i < evenLengthOldCode.length(); i += 2) {
			pin[(i / 2) + 1] = (byte) (Integer.parseInt(evenLengthOldCode
					.substring(i, i + 2), 16));
		}

		// Insert second PIN
		pin[PIN_LENGTH] = (byte) (2 * 16 + evenLengthNewCode.length());
		for (int i = 0; i < evenLengthNewCode.length(); i += 2) {
			pin[(i / 2) + 1 + PIN_LENGTH] = (byte) (Integer.parseInt(
					evenLengthNewCode.substring(i, i + 2), 16));
		}

		// Send command
		ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00, 0x24,
				0x00, 0x01 /* hardcoded reference */, pin));

		// End lock
		this.endTransaction();

		// Check whether correct
		if ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00)) {
			// Correct PIN code
			return 3;
		} else if (rAPDU.getSW1() == 0x63) {
			// See how many attempts are remaining
			throw new WrongPINException(evenLengthOldCode,
					(rAPDU.getSW2() % 16));
		} else {
			throw new InvalidSWException(rAPDU.getSW1(), rAPDU.getSW2());
		}
	}

	/**
	 * Reactivates the card by the given two PUK codes. After this, the PINs can
	 * be entered again.
	 * 
	 * TODO Can't really test this because i don't have any test cards and I
	 * don't know the municipality PUK code
	 * 
	 * @param citizen
	 *            contains the citizen PUK code
	 * @param government
	 *            contains the government PUK code
	 * @throws CardException
	 *             when a card related exception occurred
	 * @throws CardNotFoundException
	 *             when the card was not present
	 * @throws InvalidSWException
	 *             when the reactivation failed and the status words were
	 *             invalid
	 */
	public void reactivate(final String citizen, final String government)
			throws CardNotFoundException, CardException, InvalidSWException {
		String fullPUK = citizen + government;

		// PUK should have even number of characters
		if (2 * ((int) (fullPUK.length() / 2)) != fullPUK.length()) {
			fullPUK = fullPUK + "F";
		}

		// Handle the case when no connection has yet been made
		if (!isConnected()) {
			throw new CardNotFoundException(
					CardNotFoundException.CardNotFoundType.NOT_CONNECTED);
		}

		// Write protect the verification of the PIN
		this.beginTransaction();

		// Insert PIN in APDU field
		byte[] pukBytes = new byte[] { (byte) 0x2C, (byte) 0xFF, (byte) 0xFF,
				(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
		pukBytes[0] = (byte) (2 * 16 + fullPUK.length());
		for (int i = 0; i < fullPUK.length(); i += 2) {
			pukBytes[(i / 2) + 1] = (byte) (Integer.parseInt(fullPUK.substring(
					i, i + 2), 16));
		}

		// Send command
		ResponseAPDU rAPDU = this.transmitAPDU(new CommandAPDU(0x00, 0x2C,
				0x00, 0x01 /* hardcoded reference */, pukBytes));

		// End lock
		this.endTransaction();

		// Check whether correct
		if ((rAPDU.getSW1() == 0x90) && (rAPDU.getSW2() == 0x00)) {
			// Reactivation succeeded
		} else {
			throw new InvalidSWException(rAPDU.getSW1(), rAPDU.getSW2());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see be.belgium.eid.eidcommon.CardReader#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		disconnect();
		super.finalize();
	}
}
