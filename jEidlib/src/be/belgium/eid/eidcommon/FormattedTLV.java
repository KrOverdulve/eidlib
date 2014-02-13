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
package be.belgium.eid.eidcommon;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import be.belgium.eid.exceptions.TagNotFoundException;

/**
 * This class makes sure that the low-level TLV character/byte streams can be
 * formatted into more appropriate formats such as integers, dates, ... .
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
public class FormattedTLV extends TLV {

	/**
	 * Initializes the formatted TLV with its default values. To input data in
	 * the TLV we have to perform
	 * {@link be.belgium.eid.eidcommon.TLV#parse(byte[])}. Do this before
	 * trying to fetch data from the buffer.
	 */
	public FormattedTLV() {
		super();
	}

	/**
	 * Initializes the formatted TLV by parsing the given character string that
	 * represents the data.
	 * 
	 * @param characterStream
	 *            is the stream of characters to parse into a TLV map structure
	 */
	public FormattedTLV(final byte[] characterStream) {
		super(characterStream);
	}

	/**
	 * Returns the data associated with the given tag in ASCII format (meaning
	 * in an 8 bit representation). Returns null is no data is associated with
	 * the given tag.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the data associated with the given tag
	 */
	public byte[] asciiData(final byte tag) {
		return super.tagData(tag);
	}

	/**
	 * Returns the data associated with the given tag in a binary format.
	 * Returns null is no data is associated with the given tag.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the data associated with the given tag
	 */
	public byte[] binaryData(final byte tag) {
		return super.tagData(tag);
	}

	/**
	 * Returns the data associated with the given tag in a string on which all
	 * kinds of string operations can be performed. Returns null is no data is
	 * associated with the given tag.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the data associated with the given tag
	 */
	public String stringData(final byte tag) {
		if (super.tagData(tag) == null) {
			return null;
		} else {
			return new String(super.tagData(tag));
		}

	}

	/**
	 * Returns the data associated with the given tag in printable hexadecimal
	 * format. For more information about this format see
	 * {@link be.belgium.eid.eidcommon.ByteConverter#hexify(byte[])}. Returns
	 * null is no data is associated with the given tag.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the data associated with the given tag
	 */
	public String hexadecimalData(final byte tag) {
		if (super.tagData(tag) == null) {
			return null;
		} else {
			return ByteConverter.hexify(super.tagData(tag));
		}
	}

	/**
	 * Returns the date associated with the given tag. Returns null if no data
	 * is associated with the given tag. Although that few months are
	 * represented by 4 byteacters, the format has to be set as MMM since the
	 * four letter month representations are converted to English 3 byteacter
	 * month representations.<br/>
	 * <b>E.g.</b>: yyyy.MM.dd (example date: 1999.11.17), dd MMM yyyy (example
	 * date: 17 OKT 2007).
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @param format
	 *            is the format in which the date is formatted
	 * @return the date associated with the given tag
	 * @throws ParseException
	 *             indicates that the data matching the given tag does not fit
	 *             the given format
	 */
	public Date dateData(final byte tag, final String format)
			throws ParseException {
		if (super.tagData(tag) == null) {
			return null;
		} else {
			// TODO Work with locales
			
			// Since the dates stored on our card are written in the native
			// language instead of English the names of the months have to
			// be replaced by English synonyms
			final DateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
			String data = stringData(tag).toUpperCase();

			// Replace names
			final HashMap<String, String> replacements = new HashMap<String, String>();
			replacements.put("FEV", "FEB");
			replacements.put("MARS", "MAR");
			replacements.put("MAAR", "MAR");
			replacements.put("MARZ", "MAR");
			replacements.put("M..R", "MAR");
			replacements.put("AVR", "APR");
			replacements.put("MEI", "MAY");
			replacements.put("MAI", "MAY");
			replacements.put("JUIN", "JUN");
			replacements.put("JUIL", "JUL");
			replacements.put("AOUT", "AUG");
			replacements.put("SEPT", "SEP");
			replacements.put("OKT", "OCT");
			replacements.put("DEZ", "DEC");

			for (String toReplace : replacements.keySet()) {
				data = data
						.replaceFirst(toReplace, replacements.get(toReplace));
			}

			// Days must be numbered by two digits
			if (data.charAt(1) == ' ') {
				data = "0" + data;
			}

			// Now that everything is correctly set we parse and return the
			// valid date
			return (Date) formatter.parse(data);
		}
	}

	/**
	 * Returns the data associated with the given tag in an integer format.
	 * Throws an exception if the tag couldn't be found. If the data associated
	 * with the tag is too long, the leading bytes are implicitly removed. If
	 * the data associated with the tag is too short, leading 0's are inserted.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the integer associated with the given tag
	 * @throws TagNotFoundException
	 *             indicates that the given tag wasn't found
	 */
	public int integerData(final byte tag) throws TagNotFoundException {
		byte[] tagData;
		byte byte1;
		byte byte2;

		if (super.tagData(tag) == null) {
			throw new TagNotFoundException(tag);
		}

		tagData = super.tagData(tag);

		if (tagData.length < 2) {
			byte1 = '0';
		} else {
			byte1 = tagData[tagData.length - 2];
		}

		if (tagData.length < 1) {
			byte2 = '0';
		} else {
			byte2 = tagData[tagData.length - 1];
		}

		return ByteConverter.integerValue(byte1, byte2, true);
	}

	/**
	 * Returns the data associated with the given tag in a long format. Throws
	 * an exception if the tag couldn't be found. If the data associated with
	 * the tag is too long, the leading bytes are implicitly removed. If the
	 * data associated with the tag is too short, leading 0's are inserted.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the long value associated with the given tag
	 * @throws TagNotFoundException
	 *             indicates that the given tag wasn't found
	 */
	public long longData(final byte tag) throws TagNotFoundException {
		byte[] tagData;
		byte byte1;
		byte byte2;
		byte byte3;
		byte byte4;

		if (super.tagData(tag) == null) {
			throw new TagNotFoundException(tag);
		}

		tagData = super.tagData(tag);

		if (tagData.length < 4) {
			byte1 = '0';
		} else {
			byte1 = tagData[tagData.length - 4];
		}

		if (tagData.length < 3) {
			byte2 = '0';
		} else {
			byte2 = tagData[tagData.length - 3];
		}

		if (tagData.length < 2) {
			byte3 = '0';
		} else {
			byte3 = tagData[tagData.length - 2];
		}

		if (tagData.length < 1) {
			byte4 = '0';
		} else {
			byte4 = tagData[tagData.length - 1];
		}

		return ByteConverter.longValue(byte1, byte2, byte3, byte4, true);
	}
}
