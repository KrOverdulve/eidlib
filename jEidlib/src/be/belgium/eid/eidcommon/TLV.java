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

import java.util.AbstractMap;
import java.util.HashMap;

/**
 * The TLV performs operations according to the <b>Tag-Length-Value protocol</b>.
 * This is the way how the data is stored on the eID smart card. Files are
 * represented as sequences of TLV elements that consist of a tag name to
 * uniquely identify the data that is represented. This tag is followed by the
 * length of the data represented by the tag. Finally the data itself is stored
 * as a sequence of bytes. <br />
 * <b>E.g.:</b> imagine we want to store the first name and the last name of a
 * person on a smart card or any other low-level device. Then we store the name
 * 'Will Hunting' as a sequence of bytes (delimited by ;'s) as follows:
 * '0;4;87;105;108;108;1;7;72;117;110;116;105;110;103'. Here the first byte is
 * the tag for the first name, the next byte is the length of the first name and
 * the next 4 bytes are the decimal representations of the ASCII characters of
 * which the first name exists. After these bytes the same thing is done for the
 * last name which has tag 1, length 7 and 7 decimal numbers representing the
 * data. <br />
 * For more information click <a
 * href="http://en.wikipedia.org/wiki/Type-length-value" target="_blank">here</a>.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 25 Nov 2007
 */
public class TLV {

	/**
	 * contains the mapping of the TLV element tags to the data that is
	 * represented by those tags
	 */
	private transient final AbstractMap<Byte, byte[]> fTLVElements;

	/**
	 * Initializes the TLV with its default values. To input data in the TLV we
	 * have to perform {@link be.belgium.eid.eidcommon.TLV#parse(byte[])}. Do
	 * this before trying to fetch data from the buffer.
	 */
	public TLV() {
		fTLVElements = new HashMap<Byte, byte[]>();
	}

	/**
	 * Initializes the TLV by parsing the given character string that represents
	 * the data.
	 * 
	 * @param characterStream
	 *            is the stream of characters to parse into a TLV map structure
	 */
	public TLV(final byte[] characterStream) {
		fTLVElements = new HashMap<Byte, byte[]>();
		parse(characterStream);
	}

	/**
	 * Parses the input stream into a map structure to make the processing of
	 * the data easier. This method can be used multiple times with different
	 * byteacterStreams to combine multiple input byteacter streams. Watch out
	 * not to redefine keys that you don't want to redefine.
	 * 
	 * @param byteacterStream
	 *            is the stream of byteacters to parse into a TLV map structure
	 */
	public void parse(final byte[] byteacterStream) {
		// Keeps track of the current position
		int i = 0;

		// Traverse the array
		while (i < byteacterStream.length) {
			final byte tag = byteacterStream[i++];

			// We can get past the length here so check for this
			int lengthData = 0;
			if (i == byteacterStream.length) {
				// Break out of the while
				break;
			} else {
				lengthData = byteacterStream[i];
			}

			// If the tag does not exist yet or the length of the current to be
			// processed tag is larger than 0, we parse the tag, otherwise we
			// keep the old tag
			if ((tagData(tag) == null) || (lengthData > 0)) {
				byte[] data;

				// The length of the data can be longer than 255 (0xFF), when
				// that
				// happens the next byteacter is also part of the length and the
				// bytes have to be added to each other to obtain the actual
				// length
				while (byteacterStream[i] == 0xFF) {
					lengthData += byteacterStream[++i];
				}

				// Compose a new array representing the data section of the
				// current
				// TLV element
				i++;

				data = new byte[lengthData];
				for (int j = 0; j < lengthData; j++) {
					data[j] = byteacterStream[i + j];
				}

				fTLVElements.put(tag, data);
				i += lengthData;
			} else {
				// Increment past the length byteacter
				i++;
			}
		}
	}

	/**
	 * Returns the data of the given tag. Returns null if there is no data
	 * associated with this tag.
	 * 
	 * @param tag
	 *            is the tag to identify the data to return
	 * @return the data associated with the given tag
	 */
	public byte[] tagData(final byte tag) {
		return fTLVElements.get(tag);
	}

	/**
	 * Returns the map structure of the TLV input stream. The key is the so
	 * called 'tag' uniquely defining the associated data.
	 * 
	 * @return the map representing the input stream in a more structured way.
	 */
	public AbstractMap<Byte, byte[]> getTLVElements() {
		return fTLVElements;
	}
}
