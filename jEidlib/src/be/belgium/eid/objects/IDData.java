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

import java.text.ParseException;
import java.util.Date;

import be.belgium.eid.eidcommon.FormattedTLV;
import be.belgium.eid.objects.SmartCardReadable;
import be.belgium.eid.exceptions.TagNotFoundException;

/**
 * The IDData class represents the ID data of a certain beID card. This data
 * includes the personal information of the card holder such as the name, first
 * name, municipality, birth information, ... . This data also includes
 * information about the beID card itself such as the begin and end of the
 * validity, the chip number of the card and the card number, ... .
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 07 Dec 2007
 */
public class IDData implements SmartCardReadable {

	/** Contains the different kinds of sexes that exist */
	public static char fgMALE = 'M';
	public static char fgFEMALEDUTCH = 'V';
	public static char fgFEMALEOTHER = 'F';
	public static char fgREMALEEN = 'W';

	/** Contains the ID specific file attributes to read from on the smart card */
	public final static char[] fgID = { fgDataTag, fgDataTagID };

	/** Contains the maximum size (in number of bytes) that the IDData can take */
	public final static int fgMAX_RAW_LEN = 1024;

	/** Contains the signature ID specific file attributes to read from */
	public final static char[] fgSIGID = { fgDataTag, fgDataTagIDSIG };

	/** Contains the card number of the ID */
	private final String fCardNumber;

	/** Contains the chip number of the ID */
	private final String fChipNumber;

	/** Contains the date from when the ID is valid */
	private final Date fValidFrom;

	/** Contains the date to when the ID is valid */
	private final Date fValidTo;

	/** Contains the municipality where the holder of the ID lives */
	private final String fMunicipality;

	/** Contains the national number */
	private final String fNationalNumber;

	/** Contains the (last) name of the holder of the ID */
	private final String fName;

	/** Contains the 1st firstname of the holder of the ID */
	private final String fFirstname1;

	/** Contains the 3rd firstname of the holder of the ID */
	private final String fFirstname3;

	/** Contains the nationality of the holder of the ID */
	private final String fNationality;

	/** Contains the birth place of the holder of the ID */
	private final String fBirthPlace;

	/** Contains the birth date of the holder of the ID */
	private final Date fBirthDate;

	/** Contains the sex of the holder of the ID */
	private final char fSex;

	/** Contains the noble condition of the holder of the ID, this can be empty */
	private final String fNobleCondition;

	/** Contains the type of document of the ID */
	private final long fDocumentType;

	/** Indicates whether the holder of the ID is white cane */
	private final boolean fIsWhiteCane;

	/** Indicates whether the holder of the ID is yellow cane */
	private final boolean fIsYellowCane;

	/** Indicates whether the holder of the ID is an extended minority */
	private final boolean fIsExtendedMinority;

	/** Contains the hash of the photo of the holder of the ID */
	private final byte[] fHashPhoto;

	/**
	 * Parses the given stream of characters into a valid IDData object
	 * 
	 * @param characterStream
	 *            is the stream of characters to parse
	 * @return a fully initialized IDData object
	 * @throws TagNotFoundException
	 *             indicates that some of the data hasn't been read in a proper
	 *             format. If this occurs often, the ID card may be invalid
	 * @throws ParseException
	 *             indicates that some of the data hasn't been read in a proper
	 *             format. If this occurs often, the ID card may be invalid
	 */
	public static IDData parse(final byte[] characterStream)
			throws TagNotFoundException, ParseException {
		// Contains the TLV reader
		final FormattedTLV fTLV = new FormattedTLV(characterStream);

		// Contains the different status attributes of the holder of the ID
		boolean isWhiteCane = false;
		boolean isYellowCane = false;
		boolean isExtendedMinority = false;

		switch (fTLV.integerData((byte) 0x10)) {
		case 1:
			isWhiteCane = true;
			break;
		case 2:
			isExtendedMinority = true;
			break;
		case 3:
			isWhiteCane = true;
			isExtendedMinority = true;
			break;
		case 4:
			isYellowCane = true;
			break;
		case 5:
			isYellowCane = true;
			isExtendedMinority = true;
			break;
		default:
			break;
		}

		// Contains the birth date, this can take several formats,
		Date birthDate;
		try {
			birthDate = fTLV.dateData((byte) 0x0C, "dd MMM yyyy");
		} catch (final ParseException e) {
			try {
				birthDate = fTLV.dateData((byte) 0x0C, "dd MMM  yyyy");
			} catch (final ParseException e1) {
				birthDate = fTLV.dateData((byte) 0x0C, "dd.MMM.yyyy");
			}
		}
		
		return new IDData(
				fTLV.stringData((byte) 0x01), 
				fTLV.hexadecimalData((byte) 0x02),
				fTLV.dateData((byte) 0x03, "dd.MM.yyyy"), 
				fTLV.dateData((byte) 0x04, "dd.MM.yyyy"), 
				fTLV.stringData((byte) 0x05), fTLV.stringData((byte) 0x06),
				fTLV.stringData((byte) 0x07), fTLV.stringData((byte) 0x08),
				fTLV.stringData((byte) 0x09), fTLV.stringData((byte) 0x0A),
				fTLV.stringData((byte) 0x0B), birthDate,
				fTLV.stringData((byte) 0x0D).charAt(0), 
				fTLV.stringData((byte) 0x0E), fTLV.longData((byte) 0x0F),
				isWhiteCane, 
				isYellowCane, 
				isExtendedMinority, 
				fTLV.asciiData((byte) 0x11));
	}

	/**
	 * Initializes the IDData object with the given data.
	 * 
	 * @param cardNumber
	 *            is the card number of the ID
	 * @param chipNumber
	 *            is the chip number of the ID
	 * @param validFrom
	 *            is the date from when the ID is valid (must be before the
	 *            validTo input)
	 * @param validTo
	 *            is the date to when the ID is valid (must be after the
	 *            validFrom input)
	 * @param municipality
	 *            is the municipality where the holder of the ID lives
	 * @param nationalNumber
	 *            is the national number
	 * @param name
	 *            is the (last) name of the holder of the ID
	 * @param firstname1
	 *            is the 1st firstname of the holder of the ID
	 * @param firstname3
	 *            is the 3rd firstname of the holder of the ID
	 * @param nationality
	 *            is the nationality of the holder of the ID
	 * @param birthPlace
	 *            is the birth place of the holder of the ID
	 * @param birthDate
	 *            is the birth date of the holder of the ID (must be a date in
	 *            the past)
	 * @param sex
	 *            is the sex of the holder of the ID (M/F)
	 * @param nobleCondition
	 *            is the noble condition of the holder of the ID, this can be
	 *            empty
	 * @param documentType
	 *            is the type of document of the ID
	 * @param isWhiteCane
	 *            indicates whether the holder of the ID is white cane (blind of
	 *            very ill sighted)
	 * @param isYellowCane
	 *            indicates whether the holder of the ID is yellow cane
	 *            (partially sighted)
	 * @param isExtendedMinority
	 *            indicates whether the holder of the ID is an extended minority
	 * @param bs
	 *            is the hash of the photo of the holder of the ID
	 */
	public IDData(final String cardNumber, final String chipNumber,
			final Date validFrom, final Date validTo,
			final String municipality, final String nationalNumber,
			final String name, final String firstname1,
			final String firstname3, final String nationality,
			final String birthPlace, final Date birthDate, final char sex,
			final String nobleCondition, final long documentType,
			final boolean isWhiteCane, final boolean isYellowCane,
			final boolean isExtendedMinority, final byte[] bs) {
		if (validFrom.after(validTo)) {
			throw new IllegalArgumentException(
					"The validity of an ID card must be "
							+ "from a date that is earlier than the date to which it "
							+ "is valid.");
		}

		if (birthDate.after(new Date())) {
			throw new IllegalArgumentException(
					"The birth date can't be a date in the future.");
		}

		if ((sex != fgMALE) && (sex != fgFEMALEDUTCH) && (sex != fgFEMALEOTHER)
				&& (sex != fgREMALEEN)) {
			throw new IllegalArgumentException(
					"The given sex must be equal to 'M' or 'F' or 'V'");
		}

		// Initialize the data
		fCardNumber = cardNumber;
		fChipNumber = chipNumber;
		fValidFrom = validFrom;
		fValidTo = validTo;
		fMunicipality = municipality;
		fNationalNumber = nationalNumber;
		fName = name;
		fFirstname1 = firstname1;
		fFirstname3 = firstname3;
		fNationality = nationality;
		fBirthPlace = birthPlace;
		fBirthDate = birthDate;
		fSex = sex;
		fNobleCondition = nobleCondition;
		fDocumentType = documentType;
		fIsWhiteCane = isWhiteCane;
		fIsYellowCane = isYellowCane;
		fIsExtendedMinority = isExtendedMinority;
		fHashPhoto = bs.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Card number: " + fCardNumber + "\n" + 
			"Chip number: " + fChipNumber + "\n" + 
			"Valid from: " + fValidFrom + "\n" + 
			"Valid to: " + fValidTo + "\n" + 
			"Municipality: " + fMunicipality + "\n" + 
			"National number: " + fNationalNumber + "\n" + 
			"Name: " + fName + "\n" + 
			"First name 1: " + fFirstname1 + "\n" + 
			"First name 3: " + fFirstname3 + "\n" + 
			"Nationality: " + fNationality + "\n" +
			"Birth place: " + fBirthPlace + "\n" +
			"Birth date: " + fBirthDate + "\n" + 
			"Sex: " + fSex + "\n" +
			"Noble condition: " + fNobleCondition + "\n" +
			"Document type: " + fDocumentType + "\n" +
			"White cane: " + fIsWhiteCane + "\n" +
			"Second cane: " + fIsYellowCane + "\n" +
			"Extended minority: " + fIsExtendedMinority + "\n" +
			"Hash picture: " + new String(fHashPhoto) + "\n";
	}

	/**
	 * Returns the card number of the ID.
	 * 
	 * @return the card number
	 */
	public String getCardNumber() {
		return fCardNumber;
	}

	/**
	 * Returns the chip number of the ID.
	 * 
	 * @return the chip number
	 */
	public String getChipNumber() {
		return fChipNumber;
	}

	/**
	 * Returns the date from when the ID is valid.
	 * 
	 * @return the begin of the validity period
	 */
	public Date getValidFrom() {
		return fValidFrom;
	}

	/**
	 * Returns the date to when the ID is valid.
	 * 
	 * @return the end of the validity period
	 */
	public Date getValidTo() {
		return fValidTo;
	}

	/**
	 * Returns the municipality where the holder of the ID lives.
	 * 
	 * @return the municipality
	 */
	public String getMunicipality() {
		return fMunicipality;
	}

	/**
	 * Returns the national number of the holder of the ID.
	 * 
	 * @return the national number
	 */
	public String getNationalNumber() {
		return fNationalNumber;
	}

	/**
	 * Returns the last name of the holder of the ID.
	 * 
	 * @return the last name of the holder
	 */
	public String getName() {
		return fName;
	}

	/**
	 * Returns the 1st first name of the holder of the ID.
	 * 
	 * @return the 1st first name of the holder
	 */
	public String get1stFirstname() {
		return fFirstname1;
	}

	/**
	 * Returns the 3rd first name of the holder of the ID.
	 * 
	 * @return the 3rd first name of the holder
	 */
	public String get3rdFirstname() {
		return fFirstname3;
	}

	/**
	 * Returns the nationality of the holder of the ID.
	 * 
	 * @return the nationality of the holder
	 */
	public String getNationality() {
		return fNationality;
	}

	/**
	 * Returns the birth place of the holder of the ID.
	 * 
	 * @return the birth place of the holder
	 */
	public String getBirthPlace() {
		return fBirthPlace;
	}

	/**
	 * Returns the birth date of the holder of the ID.
	 * 
	 * @return the birth date of the holder
	 */
	public Date getBirthDate() {
		return fBirthDate;
	}

	/**
	 * Returns the sex of the holder of the ID.
	 * 
	 * @return the sex ('M' or 'F')
	 */
	public char getSex() {
		return fSex;
	}

	/**
	 * Returns the noble condition of the holder of the ID.
	 * 
	 * @return the noble condition of the holder
	 */
	public String getNobleCondition() {
		return fNobleCondition;
	}

	/**
	 * Returns the document type of the ID.
	 * 
	 * @return the document type
	 */
	public long getDocumentType() {
		return fDocumentType;
	}

	/**
	 * Returns whether the holder of the ID is white cane (blind or very ill
	 * sighted people).
	 * 
	 * @return whether the holder is white cane
	 */
	public boolean isWhiteCane() {
		return fIsWhiteCane;
	}

	/**
	 * Returns whether the holder of the ID is yellow cane (partially sighted
	 * people).
	 * 
	 * @return whether the holder is yellow cane
	 */
	public boolean isYellowCane() {
		return fIsYellowCane;
	}

	/**
	 * Returns whether the holder of the ID is an extended minority.
	 * 
	 * @return whether the holder is an extended minority
	 */
	public boolean isExtendedMinority() {
		return fIsExtendedMinority;
	}

	/**
	 * Returns the hash of the photo of the holder of the ID.
	 * 
	 * @return the hash of the photo
	 */
	public byte[] getHashPhoto() {
		return fHashPhoto.clone();
	}
}