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
 * The IDTokenInfo class contains the token information of the smart card.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Mar 2007
 */
public class IDTokenInfo implements SmartCardReadable {

	/** Contains the offset of the token info from the card */
	public static int fgOFFSET = 0x25;

	/** Contains the signature Token specific file attributes to read from */
	public final static byte[] fgTokenInfo = { fgCertTag, fgDataTagTOKENINFO };

	/** Contains the graphical personalisation */
	private final byte fGraphPerso;

	/** Contains the electrical personalisation */
	private final byte fElecPerso;

	/** Contains the electrical personalisation interface */
	private final byte fElecPersoInterface;

	/** Contains the reserved information */
	private final byte fReserved;

	/**
	 * Parses the given stream of final char acters into a valid IDTokenInfo
	 * object
	 * 
	 * @param characterStream is the stream of final characters to parse
	 * @return a fully initialized IDTokenInfo object
	 * @throws BufferTooSmallException
	 *             when the buffer of the final char acters was too small to get
	 *             valid data from
	 */
	public static IDTokenInfo parse(final byte[] characterStream)
			throws BufferTooSmallException {
		if (characterStream.length < (fgOFFSET + 4)) {
			throw new BufferTooSmallException();
		}

		return new IDTokenInfo(characterStream[fgOFFSET],
				characterStream[fgOFFSET + 1], characterStream[fgOFFSET + 2],
				characterStream[fgOFFSET + 3]);
	}

	/**
	 * Initializes the token information with the given data.
	 * 
	 * @param graphPerso
	 *            is the graphical personalisation byte
	 * @param elecPerso
	 *            is the electrical personalisation byte
	 * @param elecPersoInterface
	 *            is the interface byte for the electrical personalisation
	 * @param reserved
	 *            indicates whether the token is reserved or not
	 */
	public IDTokenInfo(final byte graphPerso, final byte elecPerso,
			final byte elecPersoInterface, final byte reserved) {
		fGraphPerso = graphPerso;
		fElecPerso = elecPerso;
		fElecPersoInterface = elecPersoInterface;
		fReserved = reserved;
	}

	/**
	 * Returns the graphical personalisation.
	 * 
	 * @return the graph perso
	 */
	public byte getGraphPerso() {
		return fGraphPerso;
	}

	/**
	 * Returns the electrical personalisation.
	 * 
	 * @return the elec perso
	 */
	public byte getElecPerso() {
		return fElecPerso;
	}

	/**
	 * Returns the electrical personalisation interface byte
	 * 
	 * @return the interface byte
	 */
	public byte getElecPersoInterface() {
		return fElecPersoInterface;
	}

	/**
	 * Returns whether the token information is reserved.
	 * 
	 * @return whether the token information is reserved
	 */
	public byte getReserved() {
		return fReserved;
	}

}
