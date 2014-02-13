/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.belgium.eid.beidgui;

import be.belgium.eid.eidcommon.ByteConverter;
import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.objects.IDAddress;
import be.belgium.eid.objects.IDData;
import be.belgium.eid.objects.IDPhoto;
import be.belgium.eid.objects.IDTokenInfo;
import be.belgium.eid.objects.IDVersion;
import be.belgium.eid.security.AuthenticationCertificate;
import be.belgium.eid.security.CACertificate;
import be.belgium.eid.security.CertificateChain;
import be.belgium.eid.security.CertificateStatus;
import be.belgium.eid.security.RootCertificate;
import be.belgium.eid.security.SignatureCertificate;
import be.belgium.eid.util.IconBuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * The virtual BEID class is a virtual BEID, which means that it can read
 * data from a real BEID card or from some external source and that it then
 * keeps the data in a buffer to that one can easily and more efficiently
 * read data from it (more than once) without having to contact the card 
 * again. The virtual BEID is also capable of writing it's data to an 
 * XML-file and of printing it's contents.
 * 
 * @author Kristof Overdulve
 * @version 1.0.0 26 Mar 2008
 */
public class VirtualBEID implements Printable {

    /** Contains the identity data */
    IDData fData;
    
    /** Contains the address */
    IDAddress fAddr;
    
    /** Contains the picture of the holder */
    IDPhoto fPhoto;
    
    /** Contains the version information */
    IDVersion fVersionInfo;
    
    /** Contains the certificate chain that is to be validated */
    CertificateChain fCertificateChain;
    
    /**
     * Initializes the virtual BEID by reading data from the given eID card.
     * Since the loading from a eID card can be a long operation, it is 
     * necessary to provide a progressBar to inform the user.
     * 
     * @param progressBar since
     * @param eID is the eID card to read from
     * @param verifyOCP whether to verify OCP
     * @param verifyCRL whether to verify CRL
     */
    public VirtualBEID(BeID eID, JProgressBar progressBar, 
	    boolean verifyOCSP, boolean verifyCRL) {
	reload(eID, progressBar, verifyOCSP, verifyCRL);
    }
    
    /**
     * Rereads the data from the given eID card to initialize the virtual BEID.
     * 
     * @param eID is the eID card to read from
     * @param verifyOCP whether to verify OCP
     * @param verifyCRL whether to verify CRL
     */
    public void reload(BeID eID, JProgressBar progressBar, 
	    boolean verifyOCSP, boolean verifyCRL) {
	try {
	    // Fetch all the data
	    progressBar.setValue(10);
	    progressBar.setString("Reading identity...");

	    fData = eID.getIDData();
	    progressBar.setValue(20);
	    progressBar.setString("Reading address...");

	    fAddr = eID.getIDAddress();
	    progressBar.setValue(40);
	    progressBar.setString("Reading version information...");

	    fVersionInfo = eID.getIDVersionInformation();
	    progressBar.setValue(50);
	    progressBar.setString("Reading picture...");
	    
	    fPhoto = eID.getIDPhoto();
	    progressBar.setValue(60);
	    progressBar.setString("Reading certificates...");
	    
	    fCertificateChain = eID.getCertificateChain();
	    if (verifyCRL) {
		eID.verifyCRL(fCertificateChain, eID.getNationalRegisterCertificate());
	    }
	    if (verifyOCSP) {
		eID.verifyOCSP(fCertificateChain);
	    }
	    progressBar.setValue(100);
	    
	    progressBar.setValue(0);
	} catch (Exception ex) {
	    JOptionPane.showMessageDialog(null, ex.getMessage(), "EID fout", 
		JOptionPane.ERROR_MESSAGE);
	    System.exit(1);
	}
    }
    
    /**
     * Rereads the data from the given XML-file to initialize the virtual BEID.
     * 
     * @param file is the file to read
     * @throws ParseException when the dates couldn't be parsed correctly
     * @throws DocumentException when the document is ill-formed
     */
    public void reload(File file) throws ParseException, DocumentException {
	// Read document
	SAXReader reader = new SAXReader();
        Document document = reader.read(file);
	System.out.println(file.getAbsolutePath());
	
	// Read identity data
	Node identityNode = document.selectSingleNode("//beid/identity");
	fData = new IDData(identityNode.valueOf("@cardnumber"),
		identityNode.valueOf("@chipnumber"),
		new SimpleDateFormat("dd/MM/yyyy").parse(identityNode.valueOf("@validfrom")),
		new SimpleDateFormat("dd/MM/yyyy").parse(identityNode.valueOf("@validto")),
		identityNode.valueOf("@municipality"),
		identityNode.valueOf("@nationalnumber"),
		identityNode.valueOf("@name"),
		identityNode.valueOf("@firstname1"),
		identityNode.valueOf("@firstname3"),
		identityNode.valueOf("@nationality"),
		identityNode.valueOf("@birthplace"),
		new SimpleDateFormat("dd/MM/yyyy").parse(identityNode.valueOf("@birthdate")),
		identityNode.valueOf("@sex").charAt(0),
		identityNode.valueOf("@noblecondition"),
		new Long(identityNode.valueOf("@document")),
		Boolean.valueOf(identityNode.valueOf("@whitecane")),
		Boolean.valueOf(identityNode.valueOf("@yellowcane")),
		Boolean.valueOf(identityNode.valueOf("@extendedminority")),
		ByteConverter.unhexify(identityNode.valueOf("@hashphoto")));
	
	// Read address data
	Node addressNode = document.selectSingleNode("/beid/address");
	fAddr = new IDAddress(addressNode.valueOf("@street"),
		addressNode.valueOf("@zipcode"),
		addressNode.valueOf("@municipality"));
	
	// Read version information
	Node versionNode = document.selectSingleNode("/beid/versioninfo");
	byte[] codes = ByteConverter.unhexify(versionNode.valueOf("@codes"));
	byte[] tokeninfo = ByteConverter.unhexify(versionNode.valueOf("@token"));
	fVersionInfo = new IDVersion(ByteConverter.unhexify(versionNode.valueOf("@chip")),
		codes[0], codes[1], codes[2], codes[3], codes[4], codes[5], codes[6],
		codes[7], codes[8], codes[9], codes[10], new IDTokenInfo(tokeninfo[0],
		tokeninfo[1], tokeninfo[2], tokeninfo[3]));
	
	// Read certificates
	RootCertificate rootCert = new RootCertificate(ByteConverter.unhexify(
		document.valueOf("/beid/certificates/rootcert/@contents")));
	rootCert.setStatus(CertificateStatus.valueOf(
		document.valueOf("/beid/certificates/rootcert/@status")));
	CACertificate caCert = new CACertificate(ByteConverter.unhexify(
		document.valueOf("/beid/certificates/cacert/@contents")));
	caCert.setStatus(CertificateStatus.valueOf(
		document.valueOf("/beid/certificates/cacert/@status")));
	AuthenticationCertificate authCert = new AuthenticationCertificate(ByteConverter.unhexify(
		document.valueOf("/beid/certificates/authcert/@contents")));
	authCert.setStatus(CertificateStatus.valueOf(
		document.valueOf("/beid/certificates/authcert/@status")));
	SignatureCertificate sigCert = new SignatureCertificate(ByteConverter.unhexify(
		document.valueOf("/beid/certificates/sigcert/@contents")));
	sigCert.setStatus(CertificateStatus.valueOf(
		document.valueOf("/beid/certificates/sigcert/@status")));
	fCertificateChain = new CertificateChain(rootCert, caCert, authCert, sigCert);
    }
    
    /**
     * Exports the virtual BEID to the given XML-file. This exports only the data
     * visual to the BEID grapical user interface. So for example the hash of the
     * picture of the holder is not exported.
     * 
     * @param file is the file to write to
     */
    public void exportToXML(File file) {
	try {
	    // Create document
	    Document document = DocumentHelper.createDocument();
	    Element root = document.addElement("beid");

	    // Add identity data
	    root.addElement("identity")
		    .addAttribute("cardnumber", fData.getCardNumber())
		    .addAttribute("chipnumber", fData.getChipNumber())
		    .addAttribute("validfrom", new SimpleDateFormat("dd/MM/yyyy").format(
				fData.getValidFrom()))
		    .addAttribute("validto", new SimpleDateFormat("dd/MM/yyyy").format(
				fData.getValidTo()))
		    .addAttribute("municipality", fData.getMunicipality())
		    .addAttribute("nationalnumber", fData.getNationalNumber())
		    .addAttribute("name", fData.getName())
		    .addAttribute("firstname1", fData.get1stFirstname())
		    .addAttribute("firstname3", fData.get3rdFirstname())
		    .addAttribute("nationality", fData.getNationality())
		    .addAttribute("birthplace", fData.getBirthPlace())
		    .addAttribute("birthdate", new SimpleDateFormat("dd/MM/yyyy").format(
				fData.getBirthDate()))
		    .addAttribute("sex", new Character(fData.getSex()).toString())
		    .addAttribute("noblecondition", fData.getNobleCondition())
		    .addAttribute("document", new Long(fData.getDocumentType()).toString())
		    .addAttribute("whitecane", new Boolean(fData.isWhiteCane()).toString())
		    .addAttribute("secondcane", new Boolean(fData.isYellowCane()).toString())
		    .addAttribute("extendedminority", new Boolean(fData.isExtendedMinority()).toString())
		    .addAttribute("hashphoto", ByteConverter.hexify(fData.getHashPhoto()));
	    
	    // Add address data
	    root.addElement("address")
		    .addAttribute("street", fAddr.getStreet())
		    .addAttribute("zipcode", fAddr.getZipCode())
		    .addAttribute("municipality", fAddr.getMunicipality());
	    
	    // Add version information
	    byte[] codes = new byte[] { fVersionInfo.getComponentCode(), fVersionInfo.getOSNumber(),
		    fVersionInfo.getOSVersion(), fVersionInfo.getSoftmaskNumber(), 
		    fVersionInfo.getSoftmaskVersion(), fVersionInfo.getAppletVersion(), 
		    fVersionInfo.getGlobalOSVersion(), fVersionInfo.getAppletInterfaceVersion(),
		    fVersionInfo.getPKCS1Support(), fVersionInfo.getKeyExchangeVersion(),
		    fVersionInfo.getApplicationLifeCycle()};
	    byte[] tokeninfo = new byte[] { fVersionInfo.getTokenInformation().getElecPerso(),
		    fVersionInfo.getTokenInformation().getElecPersoInterface(),
		    fVersionInfo.getTokenInformation().getGraphPerso(),
		    fVersionInfo.getTokenInformation().getReserved()};
	    root.addElement("versioninfo")
		    .addAttribute("chip", ByteConverter.hexify(fVersionInfo.getChipNumber()))
		    .addAttribute("codes", ByteConverter.hexify(codes))
		    .addAttribute("token", ByteConverter.hexify(tokeninfo));
	    
	    // Add the certificate data
	    Element certificates = root.addElement("certificates");
	    certificates.addElement("rootcert")
		.addAttribute("id", fCertificateChain.getRootCert().getID())
		.addAttribute("contents", ByteConverter.hexify(
			fCertificateChain.getRootCert().getContents()))
		.addAttribute("status", fCertificateChain.getRootCert().getStatus().toString());
	    certificates.addElement("cacert")
		.addAttribute("id", fCertificateChain.getCertificateAuthorityCert().getID())
		.addAttribute("contents", ByteConverter.hexify(
			fCertificateChain.getCertificateAuthorityCert().getContents()))
		.addAttribute("status", fCertificateChain.
			getCertificateAuthorityCert().getStatus().toString());
	    certificates.addElement("authcert")
		.addAttribute("id", fCertificateChain.getAuthenticationCert().getID())
		.addAttribute("contents", ByteConverter.hexify(
			fCertificateChain.getAuthenticationCert().getContents()))
		.addAttribute("status", fCertificateChain.getAuthenticationCert().
			getStatus().toString());
	    certificates.addElement("sigcert")
		.addAttribute("id", fCertificateChain.getSignatureCert().getID())
		.addAttribute("contents", ByteConverter.hexify(
			fCertificateChain.getSignatureCert().getContents()))
		.addAttribute("status", fCertificateChain.getSignatureCert().
			getStatus().toString());
	    
	    // Write to file
	    XMLWriter writer = new XMLWriter(new FileWriter(file), OutputFormat.createPrettyPrint());
	    writer.write(document);
	    writer.close();
	} catch (IOException ex) {
	    JOptionPane.showMessageDialog(null, ex.getMessage(), "EID fout", 
		JOptionPane.ERROR_MESSAGE);
	}
    }
    
    /**
     * Prints the contents of the BEID card to an installed printer.
     */
    public void print() {
	PrinterJob pj = PrinterJob.getPrinterJob();
	pj.setPrintable(this);

	if (pj.printDialog()) {
	    pj.defaultPage();
	    try {
		pj.print();
	    } catch (PrinterException e) {
		JOptionPane.showMessageDialog(null, e.getMessage(), "EID fout", 
		    JOptionPane.ERROR_MESSAGE);
	    }
	}
    }
    
    /*
     * (non-Javadoc)
     * @see java.awt.print.Printable#print(java.awt.Graphics, java.awt.print.PageFormat, int)
     */
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
	// We only want to print one page
	if (page > 0) {
	    return NO_SUCH_PAGE;
	}

	//Translate to printing area
	Graphics2D g2d = (Graphics2D) g;
	g2d.translate(pf.getImageableX(), pf.getImageableY());

	// Initialize some position trackers
	int yPosition = 20;
	int titlefontsize = 12;
	int fontsize = 10;
	int LEFTBORDER = 10;
	final int SECONDCOLUMN = 230;
	final int GAPSIZE = 35;


	// Draw the identity information
	g.setColor(Color.BLACK);
	g.setFont(new Font(g.getFont().getFamily(), Font.BOLD, titlefontsize));
	g.drawString("Identiteit / Identity information:", LEFTBORDER, yPosition);

	// Draw photo
	try {
	    fPhoto.writeToFile("tmp");
	    g.drawImage(IconBuilder.createImage("tmp" + IDPhoto.EXTENSION), 
		    LEFTBORDER, yPosition += titlefontsize + 2, 77 /* width */, 110 /* height */, null);
	    (new File("tmp" + IDPhoto.EXTENSION)).delete();
	} catch (final Exception e) {
	    // Ignore and don't draw image
	}
	
	// Draw the rest of the identity
	g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, fontsize));
	g.drawString("Naam / Name: ", LEFTBORDER + 85, yPosition += 10);
	g.drawString(fData.get1stFirstname() + " " + fData.getName() 
		+ " " + fData.get3rdFirstname(), SECONDCOLUMN + 85 , yPosition);
	
	g.drawString("Geboorteplaats / Birthplace:", LEFTBORDER + 85, 
		yPosition += fontsize + 1);
	g.drawString(fData.getBirthPlace(), SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Geboortedatum / Birthdate:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(new SimpleDateFormat("dd/MM/yyyy").format(fData.getBirthDate()), 
		 SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Nationaliteit / Nationality:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(fData.getNationality(), SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Geslacht / Sex:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(new Character(fData.getSex()).toString(), SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Titel / Title:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(fData.getNobleCondition(), SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Nationaal nr. / National number:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(fData.getNationalNumber(), SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Geldigheidsperiode / Valid from - until:", LEFTBORDER + 85, yPosition += fontsize + 2);
	g.drawString(new SimpleDateFormat("dd/MM/yyyy").format(fData.getValidFrom()) + 
		" - " + new SimpleDateFormat("dd/MM/yyyy").format(fData.getValidTo()),
		SECONDCOLUMN + 85, yPosition);
	
	g.drawString("Extra / Special status:", LEFTBORDER + 85, yPosition += fontsize + 2);
	
	String extra = "";
	if (fData.isWhiteCane()) {
	    extra += "white cane";
	}
	if (fData.isYellowCane()) {
	    if (!extra.equals("")) {
		extra += ", ";
	    }
	    extra += "yellow cane";
	}
	if (fData.isExtendedMinority()) {
	    if (!extra.equals("")) {
		extra += ", ";
	    }
	    extra += "extended minority";
	}
	if (extra.equals("")) {
	    g.drawString("-", SECONDCOLUMN + 85, yPosition);
	} else {
	    g.drawString(extra, SECONDCOLUMN + 85, yPosition);
	}
	
	// Draw the address data
	yPosition += GAPSIZE;
	
	g.setColor(Color.BLACK);
	g.setFont(new Font(g.getFont().getFamily(), Font.BOLD, titlefontsize));
	g.drawString("Address / Address:", LEFTBORDER, yPosition);
	
	g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, fontsize));
	drawTuple(g, "Straat / Street:", fAddr.getStreet(), LEFTBORDER, 
		SECONDCOLUMN, yPosition += titlefontsize + 5);
	drawTuple(g, "Woonplaats / City (Municipality):", fAddr.getZipCode() + " " + 
		fAddr.getMunicipality(), LEFTBORDER, SECONDCOLUMN, yPosition += fontsize + 2);
	
	// Draw the card information
	yPosition += GAPSIZE;
	
	g.setFont(new Font(g.getFont().getFamily(), Font.BOLD, titlefontsize));
	g.drawString("Informatie over de kaart / Card information:", LEFTBORDER, yPosition);
	
	g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, fontsize));
	drawTuple(g, "Chip nummer / Chip number:", fData.getChipNumber(), LEFTBORDER, 
		SECONDCOLUMN, yPosition += titlefontsize + 5);
	drawTuple(g, "Kaartnr. / Card number:", fData.getCardNumber(), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Gemeente / Issuing municipality", fData.getMunicipality(), 
		LEFTBORDER, SECONDCOLUMN, yPosition += fontsize + 2);
	
	// Draw the version information
	yPosition += GAPSIZE;
	
	g.setFont(new Font(g.getFont().getFamily(), Font.BOLD, titlefontsize));
	g.drawString("Informatie over de kaart / Card information:", LEFTBORDER, yPosition);
	
	g.setFont(new Font(g.getFont().getFamily(), Font.PLAIN, fontsize));
	drawTuple(g, "Component Code", ByteConverter.hexify(
		fVersionInfo.getComponentCode()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += titlefontsize + 5);
	drawTuple(g, "OS Number", ByteConverter.hexify(
		fVersionInfo.getOSNumber()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "OS Version", ByteConverter.hexify(
		fVersionInfo.getOSVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Softmask Number", ByteConverter.hexify(
		fVersionInfo.getSoftmaskNumber()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Softmask Version", ByteConverter.hexify(
		fVersionInfo.getSoftmaskVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Applet Version", ByteConverter.hexify(
		fVersionInfo.getAppletVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Global OS Version", ByteConverter.hexify(
		fVersionInfo.getGlobalOSVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Applet Interface Version", ByteConverter.hexify(
		fVersionInfo.getAppletInterfaceVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "PKCS1 Support", ByteConverter.hexify(
		fVersionInfo.getPKCS1Support()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Key Exchange Version", ByteConverter.hexify(
		fVersionInfo.getKeyExchangeVersion()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Application Life Cycle", ByteConverter.hexify(
		fVersionInfo.getApplicationLifeCycle()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Graphical Personalisation", ByteConverter.hexify(
		    fVersionInfo.getTokenInformation().getGraphPerso()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Electrical Personalisation", ByteConverter.hexify(
		    fVersionInfo.getTokenInformation().getElecPerso()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	drawTuple(g, "Electrical Personalisation Interface", ByteConverter.hexify(
		    fVersionInfo.getTokenInformation().getElecPersoInterface()), LEFTBORDER, 
		SECONDCOLUMN, yPosition += fontsize + 2);
	
	// Return that this page is printed
	((Graphics2D)g).setStroke(new BasicStroke());
	g.drawLine(0, yPosition + 20, new Long(Math.round(pf.getImageableWidth())).intValue(), 
		yPosition + 20);
	return Printable.PAGE_EXISTS;
    }
    
    /**
     * Draws a tuple of information.
     * 
     * @param g is the graphical panel on which to draw the information
     * @param title is the title of the tuple to draw
     * @param description is the description for the title
     * @param firstX is the left column in which the titles should be drawn
     * @param secondX is the right column where the descriptions should be drawn
     * @param yPosition is the y coordinate where to draw the information
     */
    private void drawTuple(Graphics g, String title, String description, int firstX, 
	    int secondX, int yPosition) {
	g.drawString(title, firstX, yPosition);
	g.drawString(description, secondX, yPosition);
    }
    
    /** 
     * Returns the identity data.
     * 
     * @return the data
     */
    public IDData getIdentityData() {
	return fData;
    }
    
    /**
     * Returns the address.
     * 
     * @return the address
     */
    public IDAddress getAddress() {
	return fAddr;
    }
    
    /**
     * Returns the picture of the holder.
     * 
     * @return the picture
     */
    public IDPhoto getPhoto() {
	return fPhoto;
    }
    
    /**
     * Returns the version information.
     * 
     * @return the version information
     */
    public IDVersion getVersionInformation() {
	return fVersionInfo;
    }
    
    /**
     * Returns the certificate chain that is to be validated.
     * 
     * @return the certificate chain
     */
    public CertificateChain getCertificateChain() {
	return fCertificateChain;
    }
}
