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

import be.belgium.eid.eidcommon.FormattedTLV;
import be.belgium.eid.exceptions.TagNotFoundException;

/**
 * The IDAddress class represents the address of the holder of the current beID
 * card. It includes the street, zip code and municipality.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 08 Dec 2007
 */
public class IDAddress implements SmartCardReadable {

	/** Contains the ID specific file attributes to read from on the smart card */
	public final static char[] fgADDR = { fgDataTag, fgDataTagADDR };

	/**
	 * Contains the maximum size (in number of bytes) that the IDAddress can
	 * take
	 */
	public final static int fgMAX_RAW_LEN = 512;

	/** Contains the street where the holder of the ID lives */
	private final String fStreet;

	/** Contains the zip-code of the area in which the holder of the ID lives */
	private final String fZipCode;

	/** Contains the municipality where the holder of the ID lives */
	private final String fMunicipality;

	/**
	 * Parses the given stream of characters into a valid IDAddress object
	 * 
	 * @param characterStream
	 *            is the stream of characters to parse
	 * @return a fully initialized IDAddress object
	 * @throws TagNotFoundException
	 *             indicates that some of the data hasn't been read in a proper
	 *             format. If this occurs often, the ID card may be invalid
	 * @throws ParseException
	 *             indicates that some of the data hasn't been read in a proper
	 *             format. If this occurs often, the ID card may be invalid
	 */
	public static IDAddress parse(final byte[] characterStream)
			throws TagNotFoundException {
		final FormattedTLV fTLV = new FormattedTLV(characterStream);

		return new IDAddress(fTLV.stringData((byte) 0x01), fTLV
				.stringData((byte) 0x02), fTLV.stringData((byte) 0x03));
	}

	/**
	 * Initializes the IDAddress object with the given data.
	 * 
	 * @param street
	 *            is the streeet where the holder of the ID lives
	 * @param zipCode
	 *            is the zip-code of the area where the holder of the ID lives
	 * @param municipality
	 *            is the municipality where the holder of the ID lives
	 */
	public IDAddress(final String street, final String zipCode,
			final String municipality) {
		fStreet = street;
		fZipCode = zipCode;
		fMunicipality = municipality;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Street: " + fStreet + "\n"
				+ "Zip code: " + fZipCode + "\n"
				+ "Municipality: " + fMunicipality + "\n";
	}
	
	/**
	 * Returns the street where the holder of the ID lives.
	 * 
	 * @return the street
	 */
	public String getStreet() {
		return fStreet;
	}

	/**
	 * Returns the zip code of the area where the holder of the ID lives.
	 * 
	 * @return the zip code
	 */
	public String getZipCode() {
		return fZipCode;
	}

	/**
	 * Returns the municipality where the holder of the ID lives.
	 * 
	 * @return the municipality
	 */
	public String getMunicipality() {
		return fMunicipality;
	}
}