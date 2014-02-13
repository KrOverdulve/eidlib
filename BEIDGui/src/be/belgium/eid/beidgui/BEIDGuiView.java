/*
 * BEIDGuiView.java
 */

package be.belgium.eid.beidgui;

import be.belgium.eid.eidcommon.ByteConverter;
import be.belgium.eid.eidlib.BeID;
import be.belgium.eid.eidlib.BeID.SignatureType;
import be.belgium.eid.event.CardListener;
import be.belgium.eid.exceptions.WrongPINException;
import be.belgium.eid.security.Certificate;
import be.belgium.eid.util.DynamicLengthTableModel;
import be.belgium.eid.util.ExtensionFileFilter;
import be.belgium.eid.util.IconBuilder;
import java.awt.Color;
import java.security.cert.CertificateException;
import java.text.ParseException;
import javax.swing.event.TreeSelectionEvent;
import org.dom4j.DocumentException;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * The application's main frame.
 */
public class BEIDGuiView extends FrameView {
    
    public static final String PREFERENCES = "preferences.xml";

    public BEIDGuiView(SingleFrameApplication app) {
        super(app);

        initComponents();
	initTrees();
	loadProperties();
        
        // Disable sign and verify
        signOKButton.setEnabled(false);
        verifyOKButton.setEnabled(false);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
	
	// Start the beid library
	fBEID = new BeID(true); // TODO Make this a configuration setting

	// Enable card listener
	fBEID.enableCardListener(new CardListener() {

	    /*
	     * (non-Javadoc)
	     * 
	     * @see be.belgium.eid.event.CardListener#cardInserted()
	     */
	    public void cardInserted() {
		try {
		    readData();
		    reloadFields();
		} catch (Exception ex1) {
		    showExceptionDialog(ex1);
		}
	    }

	    /*
	     * (non-Javadoc)
	     * 
	     * @see be.belgium.eid.event.CardListener#cardRemoved()
	     */
	    public void cardRemoved() {
		clearFields();
	    }

	});
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = BEIDGuiApp.getApplication().getMainFrame();
            aboutBox = new BEIDGuiAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        BEIDGuiApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        mainTabbedPanel = new javax.swing.JTabbedPane();
        mainIdentityPanel = new javax.swing.JPanel();
        headerBelgiumLabel = new javax.swing.JLabel();
        identityCardLabel = new javax.swing.JLabel();
        addressPanel = new javax.swing.JPanel();
        streetLabel = new javax.swing.JLabel();
        streetTextField = new javax.swing.JTextField();
        municipalityLabel = new javax.swing.JLabel();
        municipalityTextField = new javax.swing.JTextField();
        countryLabel = new javax.swing.JLabel();
        countryTextfield = new javax.swing.JTextField();
        cardinfoPanel = new javax.swing.JPanel();
        chipNumberPanel = new javax.swing.JLabel();
        chipNumberTextField = new javax.swing.JTextField();
        cardNumberLabel = new javax.swing.JLabel();
        cardNumberTextField = new javax.swing.JTextField();
        issmunLabel = new javax.swing.JLabel();
        issmunTextField = new javax.swing.JTextField();
        identityPanel = new javax.swing.JPanel();
        nameLabel = new javax.swing.JLabel();
        firstnameLabel = new javax.swing.JLabel();
        birthLabel = new javax.swing.JLabel();
        birthTextField = new javax.swing.JTextField();
        nationalityLabel = new javax.swing.JLabel();
        nationalityTextField = new javax.swing.JTextField();
        firstnameTextField = new javax.swing.JTextField();
        nameTextField = new javax.swing.JTextField();
        sexLabel = new javax.swing.JLabel();
        sexTextField = new javax.swing.JTextField();
        identityValidityLabel = new javax.swing.JLabel();
        identityValidityTextField = new javax.swing.JTextField();
        pictureLabel = new javax.swing.JLabel();
        nationalNumberLabel = new javax.swing.JLabel();
        nationalNumberTextField = new javax.swing.JTextField();
        specialStatusPanel = new javax.swing.JPanel();
        whitecaneCheckBox = new javax.swing.JCheckBox();
        yellowcaneCheckBox = new javax.swing.JCheckBox();
        emCheckBox = new javax.swing.JCheckBox();
        chipLabel = new javax.swing.JLabel();
        arrowLabel = new javax.swing.JLabel();
        certificatesMainPanel = new javax.swing.JPanel();
        certlistScrollPane = new javax.swing.JScrollPane();
        certlistTree = new javax.swing.JTree();
        certInformationPanel = new javax.swing.JPanel();
        ownerLabel = new javax.swing.JLabel();
        issuerLabel = new javax.swing.JLabel();
        keylengthLabel = new javax.swing.JLabel();
        validFromLabel = new javax.swing.JLabel();
        certStatusLabel = new javax.swing.JLabel();
        validUntilLabel = new javax.swing.JLabel();
        certStatusTextField = new javax.swing.JTextField();
        validUntilTextField = new javax.swing.JTextField();
        validFromTextField = new javax.swing.JTextField();
        keylengthTextField = new javax.swing.JTextField();
        issuerTextField = new javax.swing.JTextField();
        ownerTextField = new javax.swing.JTextField();
        signPanel = new javax.swing.JPanel();
        dataToSignLabel = new javax.swing.JLabel();
        outputSigLabel = new javax.swing.JLabel();
        signOKButton = new javax.swing.JButton();
        dataToSignTextfield = new javax.swing.JTextField();
        outputSigTextfield = new javax.swing.JTextField();
        browseDataToSignButton = new javax.swing.JButton();
        signPinLabel = new javax.swing.JLabel();
        pinPasswordField = new javax.swing.JPasswordField();
        verifyPanel = new javax.swing.JPanel();
        dataToVerifyLabel = new javax.swing.JLabel();
        dataToVerifyTextfield = new javax.swing.JTextField();
        browseDataToVerifyButton = new javax.swing.JButton();
        signatureLabel = new javax.swing.JLabel();
        signatureTextField = new javax.swing.JTextField();
        verifyOKButton = new javax.swing.JButton();
        browseSignatureButton = new javax.swing.JButton();
        verifyLabel = new javax.swing.JLabel();
        pinMainPanel = new javax.swing.JPanel();
        versionInfoPanel = new javax.swing.JPanel();
        versionInfoScrollPane = new javax.swing.JScrollPane();
        versionInfoTable = new javax.swing.JTable();
        pinPanel = new javax.swing.JPanel();
        changePINPanel = new javax.swing.JPanel();
        oldPINLabel = new javax.swing.JLabel();
        newPINLabel = new javax.swing.JLabel();
        confirmPINLabel = new javax.swing.JLabel();
        changePINButton = new javax.swing.JButton();
        confirmPINPasswordField = new javax.swing.JPasswordField();
        newPINPasswordField = new javax.swing.JPasswordField();
        oldPINPasswordField = new javax.swing.JPasswordField();
        msgPINLabel = new javax.swing.JLabel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        fMenubar = new javax.swing.JMenuBar();
        javax.swing.JMenu fFileMenu = new javax.swing.JMenu();
        fReadMenuItem = new javax.swing.JMenuItem();
        fOpenMenuItem = new javax.swing.JMenuItem();
        fSaveAsMenuItem = new javax.swing.JMenuItem();
        printMenuItem = new javax.swing.JMenuItem();
        fPreferencesMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem fExitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu fHelpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(be.belgium.eid.beidgui.BEIDGuiApp.class).getContext().getResourceMap(BEIDGuiView.class);
        mainPanel.setBackground(resourceMap.getColor("mainPanel.background")); // NOI18N
        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setPreferredSize(new java.awt.Dimension(820, 620));

        mainTabbedPanel.setName("mainTabbedPanel"); // NOI18N

        mainIdentityPanel.setBackground(resourceMap.getColor("mainIdentityPanel.background")); // NOI18N
        mainIdentityPanel.setName("mainIdentityPanel"); // NOI18N

        headerBelgiumLabel.setFont(resourceMap.getFont("headerBelgiumLabel.font")); // NOI18N
        headerBelgiumLabel.setText(resourceMap.getString("headerBelgiumLabel.text")); // NOI18N
        headerBelgiumLabel.setName("headerBelgiumLabel"); // NOI18N

        identityCardLabel.setText(resourceMap.getString("identityCardLabel.text")); // NOI18N
        identityCardLabel.setName("identityCardLabel"); // NOI18N

        addressPanel.setBackground(resourceMap.getColor("addressPanel.background")); // NOI18N
        addressPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("addressPanel.border.title"))); // NOI18N
        addressPanel.setName("addressPanel"); // NOI18N

        streetLabel.setText(resourceMap.getString("streetLabel.text")); // NOI18N
        streetLabel.setName("streetLabel"); // NOI18N

        streetTextField.setEditable(false);
        streetTextField.setText(resourceMap.getString("streetTextField.text")); // NOI18N
        streetTextField.setName("streetTextField"); // NOI18N

        municipalityLabel.setText(resourceMap.getString("municipalityLabel.text")); // NOI18N
        municipalityLabel.setName("municipalityLabel"); // NOI18N

        municipalityTextField.setEditable(false);
        municipalityTextField.setText(resourceMap.getString("municipalityTextField.text")); // NOI18N
        municipalityTextField.setName("municipalityTextField"); // NOI18N

        countryLabel.setText(resourceMap.getString("countryLabel.text")); // NOI18N
        countryLabel.setName("countryLabel"); // NOI18N

        countryTextfield.setEditable(false);
        countryTextfield.setText(resourceMap.getString("countryTextfield.text")); // NOI18N
        countryTextfield.setName("countryTextfield"); // NOI18N

        org.jdesktop.layout.GroupLayout addressPanelLayout = new org.jdesktop.layout.GroupLayout(addressPanel);
        addressPanel.setLayout(addressPanelLayout);
        addressPanelLayout.setHorizontalGroup(
            addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addressPanelLayout.createSequentialGroup()
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(municipalityLabel)
                    .add(countryLabel)
                    .add(streetLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addressPanelLayout.createSequentialGroup()
                        .add(countryTextfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                        .add(111, 111, 111))
                    .add(streetTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .add(municipalityTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                .addContainerGap())
        );
        addressPanelLayout.setVerticalGroup(
            addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(addressPanelLayout.createSequentialGroup()
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(streetLabel)
                    .add(streetTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(municipalityLabel)
                    .add(municipalityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(addressPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(countryLabel)
                    .add(countryTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        cardinfoPanel.setBackground(resourceMap.getColor("cardinfoPanel.background")); // NOI18N
        cardinfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("cardinfoPanel.border.title"))); // NOI18N
        cardinfoPanel.setName("cardinfoPanel"); // NOI18N

        chipNumberPanel.setText(resourceMap.getString("chipNumberPanel.text")); // NOI18N
        chipNumberPanel.setName("chipNumberPanel"); // NOI18N

        chipNumberTextField.setText(resourceMap.getString("chipNumberTextField.text")); // NOI18N
        chipNumberTextField.setName("chipNumberTextField"); // NOI18N

        cardNumberLabel.setText(resourceMap.getString("cardNumberLabel.text")); // NOI18N
        cardNumberLabel.setName("cardNumberLabel"); // NOI18N

        cardNumberTextField.setText(resourceMap.getString("cardNumberTextField.text")); // NOI18N
        cardNumberTextField.setName("cardNumberTextField"); // NOI18N

        issmunLabel.setText(resourceMap.getString("issmunLabel.text")); // NOI18N
        issmunLabel.setName("issmunLabel"); // NOI18N

        issmunTextField.setText(resourceMap.getString("issmunTextField.text")); // NOI18N
        issmunTextField.setName("issmunTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout cardinfoPanelLayout = new org.jdesktop.layout.GroupLayout(cardinfoPanel);
        cardinfoPanel.setLayout(cardinfoPanelLayout);
        cardinfoPanelLayout.setHorizontalGroup(
            cardinfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardinfoPanelLayout.createSequentialGroup()
                .add(chipNumberPanel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(chipNumberTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE))
            .add(cardinfoPanelLayout.createSequentialGroup()
                .add(cardNumberLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cardNumberTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
            .add(cardinfoPanelLayout.createSequentialGroup()
                .add(issmunLabel)
                .add(2, 2, 2)
                .add(issmunTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
        cardinfoPanelLayout.setVerticalGroup(
            cardinfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cardinfoPanelLayout.createSequentialGroup()
                .add(cardinfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chipNumberPanel)
                    .add(chipNumberTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cardinfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cardNumberLabel)
                    .add(cardNumberTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cardinfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(issmunLabel)
                    .add(issmunTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        identityPanel.setBackground(resourceMap.getColor("identityPanel.background")); // NOI18N
        identityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("identityPanel.border.title"))); // NOI18N
        identityPanel.setName("identityPanel"); // NOI18N

        nameLabel.setFont(resourceMap.getFont("nameLabel.font")); // NOI18N
        nameLabel.setText(resourceMap.getString("nameLabel.text")); // NOI18N
        nameLabel.setName("nameLabel"); // NOI18N

        firstnameLabel.setFont(resourceMap.getFont("firstnameLabel.font")); // NOI18N
        firstnameLabel.setText(resourceMap.getString("firstnameLabel.text")); // NOI18N
        firstnameLabel.setName("firstnameLabel"); // NOI18N

        birthLabel.setText(resourceMap.getString("birthLabel.text")); // NOI18N
        birthLabel.setName("birthLabel"); // NOI18N

        birthTextField.setEditable(false);
        birthTextField.setText(resourceMap.getString("birthTextField.text")); // NOI18N
        birthTextField.setName("birthTextField"); // NOI18N

        nationalityLabel.setText(resourceMap.getString("nationalityLabel.text")); // NOI18N
        nationalityLabel.setName("nationalityLabel"); // NOI18N

        nationalityTextField.setEditable(false);
        nationalityTextField.setText(resourceMap.getString("nationalityTextField.text")); // NOI18N
        nationalityTextField.setName("nationalityTextField"); // NOI18N

        firstnameTextField.setEditable(false);
        firstnameTextField.setText(resourceMap.getString("firstnameTextField.text")); // NOI18N
        firstnameTextField.setName("firstnameTextField"); // NOI18N

        nameTextField.setEditable(false);
        nameTextField.setText(resourceMap.getString("nameTextField.text")); // NOI18N
        nameTextField.setName("nameTextField"); // NOI18N

        sexLabel.setText(resourceMap.getString("sexLabel.text")); // NOI18N
        sexLabel.setName("sexLabel"); // NOI18N

        sexTextField.setEditable(false);
        sexTextField.setText(resourceMap.getString("sexTextField.text")); // NOI18N
        sexTextField.setName("sexTextField"); // NOI18N

        identityValidityLabel.setText(resourceMap.getString("identityValidityLabel.text")); // NOI18N
        identityValidityLabel.setName("identityValidityLabel"); // NOI18N

        identityValidityTextField.setEditable(false);
        identityValidityTextField.setText(resourceMap.getString("identityValidityTextField.text")); // NOI18N
        identityValidityTextField.setName("identityValidityTextField"); // NOI18N

        pictureLabel.setText(resourceMap.getString("pictureLabel.text")); // NOI18N
        pictureLabel.setName("pictureLabel"); // NOI18N

        nationalNumberLabel.setText(resourceMap.getString("nationalNumberLabel.text")); // NOI18N
        nationalNumberLabel.setName("nationalNumberLabel"); // NOI18N

        nationalNumberTextField.setEditable(false);
        nationalNumberTextField.setText(resourceMap.getString("nationalNumberTextField.text")); // NOI18N
        nationalNumberTextField.setName("nationalNumberTextField"); // NOI18N

        specialStatusPanel.setBackground(resourceMap.getColor("specialStatusPanel.background")); // NOI18N
        specialStatusPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("specialStatusPanel.border.title"))); // NOI18N
        specialStatusPanel.setName("specialStatusPanel"); // NOI18N

        whitecaneCheckBox.setText(resourceMap.getString("whitecaneCheckBox.text")); // NOI18N
        whitecaneCheckBox.setEnabled(false);
        whitecaneCheckBox.setName("whitecaneCheckBox"); // NOI18N

        yellowcaneCheckBox.setText(resourceMap.getString("yellowcaneCheckBox.text")); // NOI18N
        yellowcaneCheckBox.setEnabled(false);
        yellowcaneCheckBox.setName("yellowcaneCheckBox"); // NOI18N

        emCheckBox.setText(resourceMap.getString("emCheckBox.text")); // NOI18N
        emCheckBox.setEnabled(false);
        emCheckBox.setName("emCheckBox"); // NOI18N

        org.jdesktop.layout.GroupLayout specialStatusPanelLayout = new org.jdesktop.layout.GroupLayout(specialStatusPanel);
        specialStatusPanel.setLayout(specialStatusPanelLayout);
        specialStatusPanelLayout.setHorizontalGroup(
            specialStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(specialStatusPanelLayout.createSequentialGroup()
                .add(specialStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(yellowcaneCheckBox)
                    .add(emCheckBox)
                    .add(whitecaneCheckBox))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        specialStatusPanelLayout.setVerticalGroup(
            specialStatusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(specialStatusPanelLayout.createSequentialGroup()
                .add(whitecaneCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(yellowcaneCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(emCheckBox)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        chipLabel.setIcon(resourceMap.getIcon("chipLabel.icon")); // NOI18N
        chipLabel.setText(resourceMap.getString("chipLabel.text")); // NOI18N
        chipLabel.setToolTipText(resourceMap.getString("chipLabel.toolTipText")); // NOI18N
        chipLabel.setName("chipLabel"); // NOI18N
        chipLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chipLabelMouseClicked(evt);
            }
        });

        arrowLabel.setIcon(resourceMap.getIcon("arrowLabel.icon")); // NOI18N
        arrowLabel.setText(resourceMap.getString("arrowLabel.text")); // NOI18N
        arrowLabel.setToolTipText(resourceMap.getString("arrowLabel.toolTipText")); // NOI18N
        arrowLabel.setName("arrowLabel"); // NOI18N
        arrowLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                arrowLabelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout identityPanelLayout = new org.jdesktop.layout.GroupLayout(identityPanel);
        identityPanel.setLayout(identityPanelLayout);
        identityPanelLayout.setHorizontalGroup(
            identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(identityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, identityPanelLayout.createSequentialGroup()
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(firstnameLabel)
                            .add(nameLabel)
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(arrowLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(chipLabel)))
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(18, 18, 18)
                                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(firstnameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                    .add(birthLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                    .add(identityPanelLayout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(nationalityLabel)
                                            .add(birthTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING, nationalityTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE)))))
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(17, 17, 17)
                                .add(nameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)))
                        .add(97, 97, 97)
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(sexLabel)
                            .add(sexTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(pictureLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(identityPanelLayout.createSequentialGroup()
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(identityValidityLabel)
                            .add(identityPanelLayout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, identityValidityTextField)
                                    .add(identityPanelLayout.createSequentialGroup()
                                        .add(nationalNumberLabel)
                                        .add(2, 2, 2)
                                        .add(nationalNumberTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 161, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                        .add(18, 18, 18)
                        .add(specialStatusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(174, 174, 174)))
                .addContainerGap())
        );
        identityPanelLayout.setVerticalGroup(
            identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(identityPanelLayout.createSequentialGroup()
                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(identityPanelLayout.createSequentialGroup()
                        .add(72, 72, 72)
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(arrowLabel)
                            .add(chipLabel)))
                    .add(identityPanelLayout.createSequentialGroup()
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(nameLabel)
                                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(firstnameLabel)
                                    .add(firstnameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(7, 7, 7))
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(sexLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(sexTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)))
                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(identityPanelLayout.createSequentialGroup()
                                .add(birthLabel)
                                .add(32, 32, 32)
                                .add(nationalityLabel)
                                .add(4, 4, 4)
                                .add(nationalityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(identityPanelLayout.createSequentialGroup()
                                        .add(19, 19, 19)
                                        .add(identityValidityLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(identityValidityTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(identityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                            .add(nationalNumberLabel)
                                            .add(nationalNumberTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                    .add(specialStatusPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(pictureLabel)))
                    .add(identityPanelLayout.createSequentialGroup()
                        .add(73, 73, 73)
                        .add(birthTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout mainIdentityPanelLayout = new org.jdesktop.layout.GroupLayout(mainIdentityPanel);
        mainIdentityPanel.setLayout(mainIdentityPanelLayout);
        mainIdentityPanelLayout.setHorizontalGroup(
            mainIdentityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainIdentityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(mainIdentityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(identityPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(headerBelgiumLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 826, Short.MAX_VALUE)
                    .add(identityCardLabel)
                    .add(mainIdentityPanelLayout.createSequentialGroup()
                        .add(addressPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(cardinfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        mainIdentityPanelLayout.setVerticalGroup(
            mainIdentityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainIdentityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(headerBelgiumLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(identityCardLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(identityPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(mainIdentityPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(addressPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cardinfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(116, Short.MAX_VALUE))
        );

        mainTabbedPanel.addTab(resourceMap.getString("mainIdentityPanel.TabConstraints.tabTitle"), mainIdentityPanel); // NOI18N

        certificatesMainPanel.setBackground(resourceMap.getColor("certificatesMainPanel.background")); // NOI18N
        certificatesMainPanel.setName("certificatesMainPanel"); // NOI18N

        certlistScrollPane.setName("certlistScrollPane"); // NOI18N

        certlistTree.setName("certlistTree"); // NOI18N
        certlistScrollPane.setViewportView(certlistTree);

        certInformationPanel.setBackground(resourceMap.getColor("certInformationPanel.background")); // NOI18N
        certInformationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("certInformationPanel.border.title"))); // NOI18N
        certInformationPanel.setName("certInformationPanel"); // NOI18N

        ownerLabel.setText(resourceMap.getString("ownerLabel.text")); // NOI18N
        ownerLabel.setName("ownerLabel"); // NOI18N

        issuerLabel.setText(resourceMap.getString("issuerLabel.text")); // NOI18N
        issuerLabel.setName("issuerLabel"); // NOI18N

        keylengthLabel.setText(resourceMap.getString("keylengthLabel.text")); // NOI18N
        keylengthLabel.setName("keylengthLabel"); // NOI18N

        validFromLabel.setText(resourceMap.getString("validFromLabel.text")); // NOI18N
        validFromLabel.setName("validFromLabel"); // NOI18N

        certStatusLabel.setText(resourceMap.getString("certStatusLabel.text")); // NOI18N
        certStatusLabel.setName("certStatusLabel"); // NOI18N

        validUntilLabel.setText(resourceMap.getString("validUntilLabel.text")); // NOI18N
        validUntilLabel.setName("validUntilLabel"); // NOI18N

        certStatusTextField.setEditable(false);
        certStatusTextField.setText(resourceMap.getString("certStatusTextField.text")); // NOI18N
        certStatusTextField.setName("certStatusTextField"); // NOI18N

        validUntilTextField.setEditable(false);
        validUntilTextField.setText(resourceMap.getString("validUntilTextField.text")); // NOI18N
        validUntilTextField.setName("validUntilTextField"); // NOI18N

        validFromTextField.setEditable(false);
        validFromTextField.setText(resourceMap.getString("validFromTextField.text")); // NOI18N
        validFromTextField.setName("validFromTextField"); // NOI18N

        keylengthTextField.setEditable(false);
        keylengthTextField.setText(resourceMap.getString("keylengthTextField.text")); // NOI18N
        keylengthTextField.setName("keylengthTextField"); // NOI18N

        issuerTextField.setEditable(false);
        issuerTextField.setText(resourceMap.getString("issuerTextField.text")); // NOI18N
        issuerTextField.setName("issuerTextField"); // NOI18N

        ownerTextField.setEditable(false);
        ownerTextField.setText(resourceMap.getString("ownerTextField.text")); // NOI18N
        ownerTextField.setName("ownerTextField"); // NOI18N

        org.jdesktop.layout.GroupLayout certInformationPanelLayout = new org.jdesktop.layout.GroupLayout(certInformationPanel);
        certInformationPanel.setLayout(certInformationPanelLayout);
        certInformationPanelLayout.setHorizontalGroup(
            certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(certInformationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(issuerLabel)
                    .add(certInformationPanelLayout.createSequentialGroup()
                        .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(certStatusLabel)
                            .add(validUntilLabel)
                            .add(validFromLabel)
                            .add(keylengthLabel)
                            .add(ownerLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(issuerTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                            .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, keylengthTextField)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, validFromTextField)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, validUntilTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(ownerTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                            .add(certStatusTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))))
                .addContainerGap())
        );
        certInformationPanelLayout.setVerticalGroup(
            certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(certInformationPanelLayout.createSequentialGroup()
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(ownerLabel)
                    .add(ownerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(issuerLabel)
                    .add(issuerTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(keylengthLabel)
                    .add(keylengthTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(validFromLabel)
                    .add(validFromTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(validUntilLabel)
                    .add(validUntilTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certInformationPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(certStatusLabel)
                    .add(certStatusTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        signPanel.setBackground(resourceMap.getColor("certSignPanel.background")); // NOI18N
        signPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("certSignPanel.border.title"))); // NOI18N
        signPanel.setName("certSignPanel"); // NOI18N

        dataToSignLabel.setText(resourceMap.getString("dataToSignLabel.text")); // NOI18N
        dataToSignLabel.setName("dataToSignLabel"); // NOI18N

        outputSigLabel.setText(resourceMap.getString("outputSignatureLabel.text")); // NOI18N
        outputSigLabel.setName("outputSignatureLabel"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(be.belgium.eid.beidgui.BEIDGuiApp.class).getContext().getActionMap(BEIDGuiView.class, this);
        signOKButton.setAction(actionMap.get("signDataActionPerformed")); // NOI18N
        signOKButton.setLabel(resourceMap.getString("signOKButton.label")); // NOI18N
        signOKButton.setName("signOKButton"); // NOI18N

        dataToSignTextfield.setText(resourceMap.getString("dataToSignTextfield.text")); // NOI18N
        dataToSignTextfield.setName("dataToSignTextfield"); // NOI18N

        outputSigTextfield.setText(resourceMap.getString("outputSigTextfield.text")); // NOI18N
        outputSigTextfield.setName("outputSigTextfield"); // NOI18N

        browseDataToSignButton.setAction(actionMap.get("dataToSignActionPerformed")); // NOI18N
        browseDataToSignButton.setText(resourceMap.getString("browseDataToSignButton.text")); // NOI18N
        browseDataToSignButton.setName("browseDataToSignButton"); // NOI18N

        signPinLabel.setText(resourceMap.getString("signPinLabel.text")); // NOI18N
        signPinLabel.setName("signPinLabel"); // NOI18N

        pinPasswordField.setName("pinPasswordField"); // NOI18N
        pinPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pinPasswordFieldFocusLost(evt);
            }
        });

        org.jdesktop.layout.GroupLayout signPanelLayout = new org.jdesktop.layout.GroupLayout(signPanel);
        signPanel.setLayout(signPanelLayout);
        signPanelLayout.setHorizontalGroup(
            signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(signPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(signOKButton)
                    .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(signPanelLayout.createSequentialGroup()
                            .add(dataToSignLabel)
                            .add(28, 28, 28)
                            .add(dataToSignTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 295, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(browseDataToSignButton))
                        .add(signPanelLayout.createSequentialGroup()
                            .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(outputSigLabel)
                                .add(signPinLabel))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(pinPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(outputSigTextfield)))))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        signPanelLayout.setVerticalGroup(
            signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(signPanelLayout.createSequentialGroup()
                .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataToSignLabel)
                    .add(dataToSignTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseDataToSignButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(outputSigLabel)
                    .add(outputSigTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(signPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(signPinLabel)
                    .add(pinPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(signOKButton)
                .addContainerGap())
        );

        verifyPanel.setBackground(resourceMap.getColor("verifyPanel.background")); // NOI18N
        verifyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("verifyPanel.border.title"))); // NOI18N
        verifyPanel.setName("verifyPanel"); // NOI18N

        dataToVerifyLabel.setText(resourceMap.getString("dataToVerifyLabel.text")); // NOI18N
        dataToVerifyLabel.setName("dataToVerifyLabel"); // NOI18N

        dataToVerifyTextfield.setName("dataToVerifyTextfield"); // NOI18N

        browseDataToVerifyButton.setAction(actionMap.get("dataToVerifyActionPerformed")); // NOI18N
        browseDataToVerifyButton.setText(resourceMap.getString("browseDataToVerifyButton.text")); // NOI18N
        browseDataToVerifyButton.setName("browseDataToVerifyButton"); // NOI18N

        signatureLabel.setText(resourceMap.getString("signatureLabel.text")); // NOI18N
        signatureLabel.setName("signatureLabel"); // NOI18N

        signatureTextField.setName("signatureTextField"); // NOI18N

        verifyOKButton.setAction(actionMap.get("verifyDataActionPerformed")); // NOI18N
        verifyOKButton.setText(resourceMap.getString("verifyOKButton.text")); // NOI18N
        verifyOKButton.setName("verifyOKButton"); // NOI18N

        browseSignatureButton.setAction(actionMap.get("signatureVerifyActionPerformed")); // NOI18N
        browseSignatureButton.setLabel(resourceMap.getString("browseSignatureButton.label")); // NOI18N
        browseSignatureButton.setName("browseSignatureButton"); // NOI18N

        verifyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        verifyLabel.setText(resourceMap.getString("verifyLabel.text")); // NOI18N
        verifyLabel.setName("verifyLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout verifyPanelLayout = new org.jdesktop.layout.GroupLayout(verifyPanel);
        verifyPanel.setLayout(verifyPanelLayout);
        verifyPanelLayout.setHorizontalGroup(
            verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(verifyPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(verifyPanelLayout.createSequentialGroup()
                        .add(verifyLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(verifyOKButton))
                    .add(verifyPanelLayout.createSequentialGroup()
                        .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(dataToVerifyLabel)
                            .add(signatureLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(signatureTextField)
                            .add(dataToVerifyTextfield, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(browseSignatureButton)
                            .add(browseDataToVerifyButton))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        verifyPanelLayout.setVerticalGroup(
            verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(verifyPanelLayout.createSequentialGroup()
                .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataToVerifyLabel)
                    .add(browseDataToVerifyButton)
                    .add(dataToVerifyTextfield, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(signatureLabel)
                    .add(signatureTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseSignatureButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(verifyPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(verifyOKButton)
                    .add(verifyLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout certificatesMainPanelLayout = new org.jdesktop.layout.GroupLayout(certificatesMainPanel);
        certificatesMainPanel.setLayout(certificatesMainPanelLayout);
        certificatesMainPanelLayout.setHorizontalGroup(
            certificatesMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(certificatesMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(certlistScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 313, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(certificatesMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(signPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(certInformationPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(verifyPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        certificatesMainPanelLayout.setVerticalGroup(
            certificatesMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(certificatesMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(certificatesMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(certlistScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 565, Short.MAX_VALUE)
                    .add(certificatesMainPanelLayout.createSequentialGroup()
                        .add(certInformationPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(signPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(verifyPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        mainTabbedPanel.addTab(resourceMap.getString("certificatesMainPanel.TabConstraints.tabTitle"), certificatesMainPanel); // NOI18N

        pinMainPanel.setBackground(resourceMap.getColor("pinMainPanel.background")); // NOI18N
        pinMainPanel.setName("pinMainPanel"); // NOI18N

        versionInfoPanel.setBackground(resourceMap.getColor("versionInfoPanel.background")); // NOI18N
        versionInfoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("versionInfoPanel.border.border.border.title"))))); // NOI18N
        versionInfoPanel.setName("versionInfoPanel"); // NOI18N

        versionInfoScrollPane.setName("versionInfoScrollPane"); // NOI18N

        versionInfoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Field", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        versionInfoTable.setName("versionInfoTable"); // NOI18N
        versionInfoTable.getTableHeader().setReorderingAllowed(false);
        versionInfoScrollPane.setViewportView(versionInfoTable);

        org.jdesktop.layout.GroupLayout versionInfoPanelLayout = new org.jdesktop.layout.GroupLayout(versionInfoPanel);
        versionInfoPanel.setLayout(versionInfoPanelLayout);
        versionInfoPanelLayout.setHorizontalGroup(
            versionInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(versionInfoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );
        versionInfoPanelLayout.setVerticalGroup(
            versionInfoPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(versionInfoScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
        );

        pinPanel.setBackground(resourceMap.getColor("pinPanel.background")); // NOI18N
        pinPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("pinPanel.border.title"))); // NOI18N
        pinPanel.setName("pinPanel"); // NOI18N

        changePINPanel.setBackground(resourceMap.getColor("changePINPanel.background")); // NOI18N
        changePINPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(resourceMap.getString("changePINPanel.border.title"))); // NOI18N
        changePINPanel.setName("changePINPanel"); // NOI18N

        oldPINLabel.setText(resourceMap.getString("oldPINLabel.text")); // NOI18N
        oldPINLabel.setName("oldPINLabel"); // NOI18N

        newPINLabel.setText(resourceMap.getString("newPINLabel.text")); // NOI18N
        newPINLabel.setName("newPINLabel"); // NOI18N

        confirmPINLabel.setText(resourceMap.getString("confirmPINLabel.text")); // NOI18N
        confirmPINLabel.setName("confirmPINLabel"); // NOI18N

        changePINButton.setText(resourceMap.getString("changePINButton.text")); // NOI18N
        changePINButton.setToolTipText(resourceMap.getString("changePINButton.toolTipText")); // NOI18N
        changePINButton.setName("changePINButton"); // NOI18N
        changePINButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePINButtonActionPerformed(evt);
            }
        });

        confirmPINPasswordField.setText(resourceMap.getString("confirmPINPasswordField.text")); // NOI18N
        confirmPINPasswordField.setName("confirmPINPasswordField"); // NOI18N
        confirmPINPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                confirmPINPasswordFieldFocusLost(evt);
            }
        });

        newPINPasswordField.setText(resourceMap.getString("newPINPasswordField.text")); // NOI18N
        newPINPasswordField.setName("newPINPasswordField"); // NOI18N
        newPINPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                newPINPasswordFieldFocusLost(evt);
            }
        });

        oldPINPasswordField.setText(resourceMap.getString("oldPINPasswordField.text")); // NOI18N
        oldPINPasswordField.setName("oldPINPasswordField"); // NOI18N
        oldPINPasswordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                oldPINPasswordFieldFocusLost(evt);
            }
        });

        msgPINLabel.setText(resourceMap.getString("msgPINLabel.text")); // NOI18N
        msgPINLabel.setName("msgPINLabel"); // NOI18N

        org.jdesktop.layout.GroupLayout changePINPanelLayout = new org.jdesktop.layout.GroupLayout(changePINPanel);
        changePINPanel.setLayout(changePINPanelLayout);
        changePINPanelLayout.setHorizontalGroup(
            changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePINPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(confirmPINLabel)
                    .add(oldPINLabel)
                    .add(newPINLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(newPINPasswordField, 0, 0, Short.MAX_VALUE)
                    .add(confirmPINPasswordField, 0, 0, Short.MAX_VALUE)
                    .add(oldPINPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePINButton)
                .add(18, 18, 18)
                .add(msgPINLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 509, Short.MAX_VALUE)
                .addContainerGap())
        );
        changePINPanelLayout.setVerticalGroup(
            changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(changePINPanelLayout.createSequentialGroup()
                .add(changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(oldPINLabel)
                    .add(oldPINPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(newPINPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(newPINLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(changePINPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(confirmPINLabel)
                    .add(confirmPINPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(changePINButton)
                    .add(msgPINLabel))
                .addContainerGap(66, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout pinPanelLayout = new org.jdesktop.layout.GroupLayout(pinPanel);
        pinPanel.setLayout(pinPanelLayout);
        pinPanelLayout.setHorizontalGroup(
            pinPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pinPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(changePINPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pinPanelLayout.setVerticalGroup(
            pinPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pinPanelLayout.createSequentialGroup()
                .add(changePINPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout pinMainPanelLayout = new org.jdesktop.layout.GroupLayout(pinMainPanel);
        pinMainPanel.setLayout(pinMainPanelLayout);
        pinMainPanelLayout.setHorizontalGroup(
            pinMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, pinMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pinMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, pinPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, versionInfoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        pinMainPanelLayout.setVerticalGroup(
            pinMainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pinMainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(versionInfoPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pinPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(94, Short.MAX_VALUE))
        );

        mainTabbedPanel.addTab(resourceMap.getString("pinMainPanel.TabConstraints.tabTitle"), pinMainPanel); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        org.jdesktop.layout.GroupLayout mainPanelLayout = new org.jdesktop.layout.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 831, Short.MAX_VALUE)
                .addContainerGap())
            .add(mainTabbedPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 851, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mainPanelLayout.createSequentialGroup()
                .add(mainTabbedPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 615, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(statusPanelSeparator, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1, Short.MAX_VALUE)
                .addContainerGap())
        );

        fMenubar.setName("fMenubar"); // NOI18N

        fFileMenu.setText(resourceMap.getString("fFileMenu.text")); // NOI18N
        fFileMenu.setName("fFileMenu"); // NOI18N

        fReadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        fReadMenuItem.setText(resourceMap.getString("fReadMenuItem.text")); // NOI18N
        fReadMenuItem.setName("fReadMenuItem"); // NOI18N
        fReadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fReadMenuItemActionPerformed(evt);
            }
        });
        fFileMenu.add(fReadMenuItem);

        fOpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        fOpenMenuItem.setText(resourceMap.getString("fOpenMenuItem.text")); // NOI18N
        fOpenMenuItem.setName("fOpenMenuItem"); // NOI18N
        fOpenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fOpenMenuItemActionPerformed(evt);
            }
        });
        fFileMenu.add(fOpenMenuItem);

        fSaveAsMenuItem.setText(resourceMap.getString("fSaveAsMenuItem.text")); // NOI18N
        fSaveAsMenuItem.setName("fSaveAsMenuItem"); // NOI18N
        fSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fSaveAsMenuItemActionPerformed(evt);
            }
        });
        fFileMenu.add(fSaveAsMenuItem);

        printMenuItem.setText(resourceMap.getString("printMenuItem.text")); // NOI18N
        printMenuItem.setName("printMenuItem"); // NOI18N
        printMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMenuItemActionPerformed(evt);
            }
        });
        fFileMenu.add(printMenuItem);

        fPreferencesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        fPreferencesMenuItem.setText(resourceMap.getString("fPreferencesMenuItem.text")); // NOI18N
        fPreferencesMenuItem.setName("fPreferencesMenuItem"); // NOI18N
        fPreferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fPreferencesMenuItemActionPerformed(evt);
            }
        });
        fFileMenu.add(fPreferencesMenuItem);

        fExitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        fExitMenuItem.setName("fExitMenuItem"); // NOI18N
        fFileMenu.add(fExitMenuItem);

        fMenubar.add(fFileMenu);

        fHelpMenu.setText(resourceMap.getString("fHelpMenu.text")); // NOI18N
        fHelpMenu.setName("fHelpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        fHelpMenu.add(aboutMenuItem);

        fMenubar.add(fHelpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        org.jdesktop.layout.GroupLayout statusPanelLayout = new org.jdesktop.layout.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(statusMessageLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 681, Short.MAX_VALUE)
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .add(statusPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(statusMessageLabel)
                    .add(statusAnimationLabel)
                    .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(fMenubar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

        private void loadProperties() {
	try {
	    Properties prop = new Properties();
	    prop.loadFromXML(new FileInputStream(PREFERENCES));
	    fVerifyOCSP = Boolean.valueOf(prop.getProperty("OCSP"));
	    fVerifyCRL = Boolean.valueOf(prop.getProperty("CRL"));
	} catch (Exception ex) {
	    fVerifyOCSP = false;
	    fVerifyCRL = false;
	}
    }
    
    /**
     * Initializes the trees.
     */
    private void initTrees() {
	// Set tree node

	rootCertlistTree = new DefaultMutableTreeNode("BELPIC");
	certlistTree.setModel(new DefaultTreeModel(rootCertlistTree));
	certlistTree.getSelectionModel().setSelectionMode(
		TreeSelectionModel.SINGLE_TREE_SELECTION);
	certlistTree.addTreeSelectionListener(new TreeSelectionListener() {
	    
	    public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
		    certlistTree.getLastSelectedPathComponent();
		
		boolean found = false;
		if (node != null) {
		    String label = node.getUserObject().toString();
		    
		    if (!label.equals("BELPIC")) {
			// Search selected label
			List<Certificate> certs = new ArrayList<Certificate>();
			try {
			    certs.add(fVirtualBEID.getCertificateChain().getRootCert());
			    certs.add(fVirtualBEID.getCertificateChain().getCertificateAuthorityCert());
			    certs.add(fVirtualBEID.getCertificateChain().getAuthenticationCert());
			    certs.add(fVirtualBEID.getCertificateChain().getSignatureCert());
			} catch (Exception ex2) {
			    showExceptionDialog(ex2);
			}

			for (Certificate cert : certs) {
			    try {
				X509Certificate x509 = cert.getX509Certificate();

				String subjName = x509.getSubjectX500Principal().getName();

				if (label.equals(subjName.substring(
					subjName.indexOf("CN=") + 3).split(",")[0])) {
				    found = true;

				    ownerTextField.setText(label);

				    String issuer = x509.getIssuerX500Principal().getName();
				    issuerTextField.setText(issuer.substring(
					    issuer.indexOf("CN=") + 3).split(",")[0]);

				    if (issuer.contains("Root")) {
					keylengthTextField.setText("2048 bits");
                                        
                                        // Disable sign and verify
                                        signOKButton.setEnabled(false);
                                        verifyOKButton.setEnabled(false);
				    } else {
					keylengthTextField.setText("1024 bits");
                                        
                                        // Enable sign and verify
                                        signOKButton.setEnabled(true);
                                        verifyOKButton.setEnabled(true);
				    }
				    validFromTextField.setText(
					    new SimpleDateFormat("dd/MM/yyyy").format(
					    x509.getNotBefore()));
				    validUntilTextField.setText(
					    new SimpleDateFormat("dd/MM/yyyy").format(
					    x509.getNotAfter()));

				    certStatusTextField.setText(cert.getStatus().toString().
					    substring(("BEID_CERTSTATUS_").length()));

				    break;
				}
                              
			    } catch (IOException ex) {
				showExceptionDialog(ex);
			    } catch (CertificateException ex) {
				showExceptionDialog(ex);
			    }
			}
		    }
		}
		
		// If none found clear fields
		if (!found) {
		    ownerTextField.setText("");
		    issuerTextField.setText("");
		    keylengthTextField.setText("");
		    validFromTextField.setText("");
		    validUntilTextField.setText("");
		    certStatusTextField.setText("");
		}
		
		pinCheck();
	    }
	});	
	
	// Change icons
	try {
	    ImageIcon rootIcon = IconBuilder.createImageIcon("cardreadericon.png");
	    ImageIcon leafIcon = IconBuilder.createImageIcon("certificateicon.png");
	    
	    DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	    renderer.setOpenIcon(rootIcon);
	    renderer.setClosedIcon(rootIcon);
	    renderer.setLeafIcon(leafIcon);
	    
	    certlistTree.setCellRenderer(renderer);
	} catch (IOException ex) {
	    // Ignore and just don't show special icons
	}
    }
    
    public void setOCSP(boolean verifyOCSP) {
	fVerifyOCSP = verifyOCSP;
    }
    
    public void setCRL(boolean verifyCRL) {
	fVerifyCRL = verifyCRL;
    }
    
    private void fPreferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fPreferencesMenuItemActionPerformed
	if (preferencesBox == null) {
            JFrame mainFrame = BEIDGuiApp.getApplication().getMainFrame();
            preferencesBox = new SettingsDialog(mainFrame, true);
            preferencesBox.setLocationRelativeTo(mainFrame);
        }
        BEIDGuiApp.getApplication().show(preferencesBox);
	setOCSP(((SettingsDialog) preferencesBox).getVerifyOCSP());
	setCRL(((SettingsDialog) preferencesBox).getVerifyCRL());
	
}//GEN-LAST:event_fPreferencesMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void chipLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chipLabelMouseClicked
	readData();
	reloadFields();
}//GEN-LAST:event_chipLabelMouseClicked

    private void arrowLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_arrowLabelMouseClicked
	clearFields();
    }//GEN-LAST:event_arrowLabelMouseClicked

    private void oldPINPasswordFieldFocusLost(java.awt.event.FocusEvent evt) {
	pinCheck();
    }
    
    private void newPINPasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_newPINPasswordFieldFocusLost
	pinCheck();
    }//GEN-LAST:event_newPINPasswordFieldFocusLost

    private void confirmPINPasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_confirmPINPasswordFieldFocusLost
	pinCheck();
    }//GEN-LAST:event_confirmPINPasswordFieldFocusLost

    private void fSaveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fSaveAsMenuItemActionPerformed
	JFileChooser fc = new JFileChooser();
	ExtensionFileFilter xmlFilter = new ExtensionFileFilter("XML files", ".xml");
        fc.addChoosableFileFilter(xmlFilter);
        fc.setFileFilter(xmlFilter);
	
	int returnVal = fc.showSaveDialog(BEIDGuiApp.getApplication().getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
		final String EXTENSION = ".xml";
		
		// Append xml if necessary
		String filename = fc.getSelectedFile().getAbsolutePath();
		if (filename.toLowerCase().indexOf(EXTENSION) != 
			filename.length() - EXTENSION.length()) {
		    filename += EXTENSION;
		}
		fVirtualBEID.exportToXML(new File(filename));
            }
    }//GEN-LAST:event_fSaveAsMenuItemActionPerformed

    private void fOpenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fOpenMenuItemActionPerformed
	JFileChooser fc = new JFileChooser();
	ExtensionFileFilter xmlFilter = new ExtensionFileFilter("XML files", "xml");
        fc.addChoosableFileFilter(xmlFilter);
        fc.setFileFilter(xmlFilter);
	
	int returnVal = fc.showOpenDialog(BEIDGuiApp.getApplication().getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
	    try {
		final String EXTENSION = ".xml";

		// Append xml if necessary
		String filename = fc.getSelectedFile().getAbsolutePath();
		if (filename.toLowerCase().indexOf(EXTENSION) != filename.length() - EXTENSION.length()) {
		    filename += EXTENSION;
		}
		fVirtualBEID.reload(new File(filename));
		
		// Reload fields
		reloadFields();
	    } catch (ParseException ex) {
		showExceptionDialog(ex);
	    } catch (DocumentException ex) {
		showExceptionDialog(ex);
	    }
	}
    }//GEN-LAST:event_fOpenMenuItemActionPerformed

    private void fReadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fReadMenuItemActionPerformed
	readData();
	reloadFields();
    }//GEN-LAST:event_fReadMenuItemActionPerformed

    private void printMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printMenuItemActionPerformed
	fVirtualBEID.print();
    }//GEN-LAST:event_printMenuItemActionPerformed

private void pinPasswordFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pinPasswordFieldFocusLost
// TODO add your handling code here:
}//GEN-LAST:event_pinPasswordFieldFocusLost

    private void changePINButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                
        System.out.println("err");
	try {
	    fBEID.changePIN(new String(oldPINPasswordField.getPassword()), 
		    new String(newPINPasswordField.getPassword()));
	    // Reset fields
	    oldPINPasswordField.setText("");
	    newPINPasswordField.setText("");
	    confirmPINPasswordField.setText("");
	    
	    msgPINLabel.setForeground(new Color(0, 150, 0));
	    msgPINLabel.setText("PIN succesfully changed.");
        } catch (WrongPINException ex) {
            msgPINLabel.setForeground(Color.RED);
		msgPINLabel.setText("PIN was invalid (" + ex.getNumberOfTriesLeft() 
			+ " tries remaining).");
        } catch (Exception ex) {
            showExceptionDialog(ex);
        }
    }

    /**
     * Handles when focus is lost at PIN structures.
     */
    private void pinCheck() {
	if (validPINEntries()) {
	    changePINButton.setEnabled(true);
	} else {
	    changePINButton.setEnabled(false);
	}
    }
    
    /**
     * Reads the data
     */
    private void readData() {
	try {
	    // Fetch all the data
	    progressBar.setVisible(true);
	    if (fVirtualBEID == null) {
		fVirtualBEID = new VirtualBEID(fBEID, progressBar, 
			fVerifyOCSP, fVerifyCRL);
	    } else {
		fVirtualBEID.reload(fBEID, progressBar, fVerifyOCSP, fVerifyCRL);
	    }
	    progressBar.setVisible(false);
	} catch (Exception ex) {
	    showExceptionDialog(ex);
	}
    }
    
    /** 
     * Reloads all the fields.
     */
    private void reloadFields() {
	// Check some error handling
	assert (fVirtualBEID.getIdentityData() != null);
	assert (fVirtualBEID.getAddress() != null);
	assert (fVirtualBEID.getPhoto() != null);
	assert (fVirtualBEID.getVersionInformation() != null);
	assert (fVirtualBEID.getCertificateChain() != null);
	
	// clear the fields
	clearFields();
	
	// Reload fields
	try {
	    // Fill the identity fields
	    birthTextField.setText(fVirtualBEID.getIdentityData().getBirthPlace() + " / " + 
		    new java.text.SimpleDateFormat("dd/MM/yyyy").format(
		    fVirtualBEID.getIdentityData().getBirthDate()));
	    cardNumberTextField.setText(fVirtualBEID.getIdentityData().getCardNumber());
	    chipNumberTextField.setText(fVirtualBEID.getIdentityData().getChipNumber());
	    countryTextfield.setText("Belgium");
	    firstnameTextField.setText(fVirtualBEID.getIdentityData().get1stFirstname() + " " + 
		    fVirtualBEID.getIdentityData().get3rdFirstname());
	    nameTextField.setText(fVirtualBEID.getIdentityData().getName());
	    identityValidityTextField.setText(
		    new java.text.SimpleDateFormat("dd/MM/yyyy").format(
		    fVirtualBEID.getIdentityData().getValidFrom())
		    + " - " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(
		    fVirtualBEID.getIdentityData().getValidTo()));
	    issmunTextField.setText(fVirtualBEID.getIdentityData().getMunicipality());
	    nationalNumberTextField.setText(fVirtualBEID.getIdentityData().getNationalNumber());
	    sexTextField.setText(new Character(fVirtualBEID.getIdentityData().getSex()).toString());
	    streetTextField.setText(fVirtualBEID.getAddress().getStreet());
	    municipalityTextField.setText(fVirtualBEID.getAddress().getZipCode() + " " + 
		    fVirtualBEID.getAddress().getMunicipality());
	    nationalityTextField.setText(fVirtualBEID.getIdentityData().getNationality());
	    pictureLabel.setIcon(new ImageIcon(fVirtualBEID.getPhoto().getImage()));
	    
	    if (fVirtualBEID.getIdentityData().isWhiteCane()) {
		whitecaneCheckBox.setSelected(true);
	    }
	    
	    if (fVirtualBEID.getIdentityData().isYellowCane()) {
		yellowcaneCheckBox.setSelected(true);
	    }
	    
	    if (fVirtualBEID.getIdentityData().isExtendedMinority()) {
		emCheckBox.setSelected(true);
	    }
	    
	    // Fill the certificate data
	    String rootName = fVirtualBEID.getCertificateChain().getRootCert().
		    getX509Certificate().getSubjectX500Principal().getName();
	    DefaultMutableTreeNode certRootnode = new DefaultMutableTreeNode(
		    rootName.substring(rootName.indexOf("CN=") + 3).split(",")[0]);
	    
	    String caName = fVirtualBEID.getCertificateChain().getCertificateAuthorityCert().
		    getX509Certificate().getSubjectX500Principal().getName();
	    DefaultMutableTreeNode certCAnode = new DefaultMutableTreeNode(
		    caName.substring(caName.indexOf("CN=") + 3).split(",")[0]);
	    
	    String authName = fVirtualBEID.getCertificateChain().getAuthenticationCert().
		    getX509Certificate().getSubjectX500Principal().getName();
	    DefaultMutableTreeNode certAuthnode = new DefaultMutableTreeNode(
		    authName.substring(authName.indexOf("CN=") + 3).split(",")[0]);
	    
	    String sigName = fVirtualBEID.getCertificateChain().getSignatureCert().
		    getX509Certificate().getSubjectX500Principal().getName();
	    DefaultMutableTreeNode certSignode = new DefaultMutableTreeNode(
		    sigName.substring(sigName.indexOf("CN=") + 3).split(",")[0]);
	    
	    certCAnode.add(certAuthnode);
	    certCAnode.add(certSignode);
	    certRootnode.add(certCAnode);
	    rootCertlistTree.add(certRootnode);
	    for (int i = 0; i < certlistTree.getRowCount(); i++) {	// Expand all rows
		certlistTree.expandRow(i);
	    }
	    	    
	    // Fill the version information table
	    versionTableModel.addRow(new Object[] {"Chip Number", 
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getChipNumber())});
	    versionTableModel.addRow(new Object[] {"Component Code",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getComponentCode())});
	    versionTableModel.addRow(new Object[] {"OS Number",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getOSNumber())});
	    versionTableModel.addRow(new Object[] {"OS Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getOSVersion())});
	    versionTableModel.addRow(new Object[] {"Softmask Number", 
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getSoftmaskNumber())});
	    versionTableModel.addRow(new Object[] {"Softmask Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getSoftmaskVersion())});
	    versionTableModel.addRow(new Object[] {"Applet Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getAppletVersion())});
	    versionTableModel.addRow(new Object[] {"Global OS Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getGlobalOSVersion())});
	    versionTableModel.addRow(new Object[] {"Applet Interface Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getAppletInterfaceVersion())});
	    versionTableModel.addRow(new Object[] {"PKCS1 Support",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getPKCS1Support())});
	    versionTableModel.addRow(new Object[] {"Key Exchange Version",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getKeyExchangeVersion())});
	    versionTableModel.addRow(new Object[] {"Application Life Cycle",
		    ByteConverter.hexify(fVirtualBEID.getVersionInformation().getApplicationLifeCycle())});
	    versionTableModel.addRow(new Object[] {"Graphical Personalisation",
		    ByteConverter.hexify(
		    fVirtualBEID.getVersionInformation().getTokenInformation().getGraphPerso())});
	    versionTableModel.addRow(new Object[] {"Electrical Personalisation",
		    ByteConverter.hexify(
		    fVirtualBEID.getVersionInformation().getTokenInformation().getElecPerso())});
	    versionTableModel.addRow(new Object[] {"Electrical Personalisation Interface", 
		    ByteConverter.hexify(
		    fVirtualBEID.getVersionInformation().getTokenInformation().getElecPersoInterface())});
	    versionTableModel.fireTableRowsInserted(0, 15);
	} catch (Exception e) {
	    showExceptionDialog(e);
	}
    }
    
    /**
     * Clears all the fields.
     */
    private void clearFields() {
	// Clear identity fields
	progressBar.setValue(0);
	birthTextField.setText("");
	cardNumberTextField.setText("");
	chipNumberTextField.setText("");
	countryTextfield.setText("");
	firstnameTextField.setText("");
	nameTextField.setText("");
	identityValidityTextField.setText("");
	issmunTextField.setText("");
	nationalNumberTextField.setText("");
	sexTextField.setText("");
	streetTextField.setText("");
	municipalityTextField.setText("");
	nationalityTextField.setText("");
	whitecaneCheckBox.setSelected(false);
	yellowcaneCheckBox.setSelected(false);
	emCheckBox.setSelected(false);
	try {
	    pictureLabel.setIcon(IconBuilder.createImageIcon("defaultimage.png"));
	}  catch (IOException ex) {
	    // Ignore
	}
	
	changePINButton.setEnabled(false);
	versionInfoTable.removeAll();
	versionTableModel = new DynamicLengthTableModel(
		new String[] {"Field", "Value"}, false);
	versionInfoTable.setModel(versionTableModel);
	for (int i = 0; i < versionTableModel.getRowCount(); i++) {
	    versionTableModel.deleteRow(0);
	}
	versionTableModel.fireTableRowsDeleted(0, versionTableModel.getRowCount());
	
	// Clear certificate fields
	rootCertlistTree.removeAllChildren();
	((DefaultTreeModel) certlistTree.getModel()).reload();
	
	certlistTree.repaint();
	ownerTextField.setText("");
	issuerTextField.setText("");
	keylengthTextField.setText("");
	validFromTextField.setText("");
	validUntilTextField.setText("");
	certStatusTextField.setText("");
	
    }
    
    /**
     * Checks whether the entered textfields for the PIN are valid.
     * 
     * @return whether the textfields are valid or not
     */
    private boolean validPINEntries() {
	return (!new String(oldPINPasswordField.getPassword()).equals("") && 
		new String(newPINPasswordField.getPassword()).equals(
			new String(confirmPINPasswordField.getPassword())));
    }
    
    /**
     * Shows an exception in a dialog.
     * 
     * @param ex is the exception to display
     */
    private void showExceptionDialog(Exception ex) {
	JOptionPane.showMessageDialog(super.getFrame(), ex.getMessage(), "EID fout", 
		JOptionPane.ERROR_MESSAGE);
	System.exit(1);
    }

    @Action
    public void dataToSignActionPerformed() {
        JFileChooser fc = new JFileChooser();
	
	int returnVal = fc.showOpenDialog(BEIDGuiApp.getApplication().getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dataToSignTextfield.setText(fc.getSelectedFile().getAbsolutePath());
	}
    }

    @Action
    public void dataToVerifyActionPerformed() {
        JFileChooser fc = new JFileChooser();
	
	int returnVal = fc.showOpenDialog(BEIDGuiApp.getApplication().getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dataToVerifyTextfield.setText(fc.getSelectedFile().getAbsolutePath());
	}
    }

    @Action
    public void signatureVerifyActionPerformed() {
        JFileChooser fc = new JFileChooser();
	
	int returnVal = fc.showOpenDialog(BEIDGuiApp.getApplication().getMainFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            signatureTextField.setText(fc.getSelectedFile().getAbsolutePath());
	}
    }

    @Action
    public void signDataActionPerformed() {
        if (dataToSignTextfield.getText().equals("") || 
                outputSigTextfield.getText().equals("") || 
                new String(pinPasswordField.getPassword()).equals("")) {     
            JOptionPane.showMessageDialog(BEIDGuiApp.getApplication().getMainFrame(), 
                    "Fill in all fields!");
        } else {
            try {
                // Read data and store in byte array
                File file = new File(dataToSignTextfield.getText());
                InputStream is = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                is.read(data);
                is.close();

                byte[] sig;
                if (ownerTextField.getText().contains("Authentication")) {
                    sig = fBEID.generateSignature(data, new String(pinPasswordField.getPassword()),
                            SignatureType.AUTHENTICATIONSIG);
                } else {
                    sig = fBEID.generateSignature(data, new String(pinPasswordField.getPassword()),
                            SignatureType.NONREPUDIATIONSIG);
                }
                
                FileOutputStream fos = new FileOutputStream(outputSigTextfield.getText());
                fos.write(sig);
                
                dataToSignTextfield.setText("");
                outputSigTextfield.setText("");
                pinPasswordField.setText("");
            } catch (Exception e) {
                showExceptionDialog(e);
            }
        }
    }

    @Action
    public void verifyDataActionPerformed() {
        if (dataToVerifyTextfield.getText().equals("") || 
                signatureTextField.getText().equals("")) {
            JOptionPane.showMessageDialog(BEIDGuiApp.getApplication().getMainFrame(), 
                    "Fill in all fields!");
        } else {
            try {
                // Read data and store in byte array
                File file = new File(dataToVerifyTextfield.getText());
                InputStream is = new FileInputStream(file);
                byte[] data = new byte[(int) file.length()];
                is.read(data);
                is.close();
                
                // Read signature and store in byte array
                file = new File(signatureTextField.getText());
                is = new FileInputStream(file);
                byte[] sig = new byte[(int) file.length()];
                is.read(sig);
                is.close();

                // Verify
                boolean verified = false;
                if (ownerTextField.getText().contains("Authentication")) {
                    verified = fBEID.verifySignature(data, sig, 
                            SignatureType.AUTHENTICATIONSIG);
                    
                } else {
                    verified = fBEID.verifySignature(data, sig, 
                            SignatureType.NONREPUDIATIONSIG);
                }
                
                // Set result
                if (verified) {
                    verifyLabel.setForeground(new Color(0, 150, 0));
                    verifyLabel.setText("Data succesfully verified.");
                    
                    dataToVerifyTextfield.setText("");
                    signatureTextField.setText("");
                } else {
                    verifyLabel.setForeground(Color.RED);
                    verifyLabel.setText("Verification failed.");
                }
            } catch (Exception e) {
                showExceptionDialog(e);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addressPanel;
    private javax.swing.JLabel arrowLabel;
    private javax.swing.JLabel birthLabel;
    private javax.swing.JTextField birthTextField;
    private javax.swing.JButton browseDataToSignButton;
    private javax.swing.JButton browseDataToVerifyButton;
    private javax.swing.JButton browseSignatureButton;
    private javax.swing.JLabel cardNumberLabel;
    private javax.swing.JTextField cardNumberTextField;
    private javax.swing.JPanel cardinfoPanel;
    private javax.swing.JPanel certInformationPanel;
    private javax.swing.JLabel certStatusLabel;
    private javax.swing.JTextField certStatusTextField;
    private javax.swing.JPanel certificatesMainPanel;
    private javax.swing.JScrollPane certlistScrollPane;
    private javax.swing.JTree certlistTree;
    private javax.swing.JButton changePINButton;
    private javax.swing.JPanel changePINPanel;
    private javax.swing.JLabel chipLabel;
    private javax.swing.JLabel chipNumberPanel;
    private javax.swing.JTextField chipNumberTextField;
    private javax.swing.JLabel confirmPINLabel;
    private javax.swing.JPasswordField confirmPINPasswordField;
    private javax.swing.JLabel countryLabel;
    private javax.swing.JTextField countryTextfield;
    private javax.swing.JLabel dataToSignLabel;
    private javax.swing.JTextField dataToSignTextfield;
    private javax.swing.JLabel dataToVerifyLabel;
    private javax.swing.JTextField dataToVerifyTextfield;
    private javax.swing.JCheckBox emCheckBox;
    private javax.swing.JMenuBar fMenubar;
    private javax.swing.JMenuItem fOpenMenuItem;
    private javax.swing.JMenuItem fPreferencesMenuItem;
    private javax.swing.JMenuItem fReadMenuItem;
    private javax.swing.JMenuItem fSaveAsMenuItem;
    private javax.swing.JLabel firstnameLabel;
    private javax.swing.JTextField firstnameTextField;
    private javax.swing.JLabel headerBelgiumLabel;
    private javax.swing.JLabel identityCardLabel;
    private javax.swing.JPanel identityPanel;
    private javax.swing.JLabel identityValidityLabel;
    private javax.swing.JTextField identityValidityTextField;
    private javax.swing.JLabel issmunLabel;
    private javax.swing.JTextField issmunTextField;
    private javax.swing.JLabel issuerLabel;
    private javax.swing.JTextField issuerTextField;
    private javax.swing.JLabel keylengthLabel;
    private javax.swing.JTextField keylengthTextField;
    private javax.swing.JPanel mainIdentityPanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTabbedPane mainTabbedPanel;
    private javax.swing.JLabel msgPINLabel;
    private javax.swing.JLabel municipalityLabel;
    private javax.swing.JTextField municipalityTextField;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel nationalNumberLabel;
    private javax.swing.JTextField nationalNumberTextField;
    private javax.swing.JLabel nationalityLabel;
    private javax.swing.JTextField nationalityTextField;
    private javax.swing.JLabel newPINLabel;
    private javax.swing.JPasswordField newPINPasswordField;
    private javax.swing.JLabel oldPINLabel;
    private javax.swing.JPasswordField oldPINPasswordField;
    private javax.swing.JLabel outputSigLabel;
    private javax.swing.JTextField outputSigTextfield;
    private javax.swing.JLabel ownerLabel;
    private javax.swing.JTextField ownerTextField;
    private javax.swing.JLabel pictureLabel;
    private javax.swing.JPanel pinMainPanel;
    private javax.swing.JPanel pinPanel;
    private javax.swing.JPasswordField pinPasswordField;
    private javax.swing.JMenuItem printMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel sexLabel;
    private javax.swing.JTextField sexTextField;
    private javax.swing.JButton signOKButton;
    private javax.swing.JPanel signPanel;
    private javax.swing.JLabel signPinLabel;
    private javax.swing.JLabel signatureLabel;
    private javax.swing.JTextField signatureTextField;
    private javax.swing.JPanel specialStatusPanel;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JLabel streetLabel;
    private javax.swing.JTextField streetTextField;
    private javax.swing.JLabel validFromLabel;
    private javax.swing.JTextField validFromTextField;
    private javax.swing.JLabel validUntilLabel;
    private javax.swing.JTextField validUntilTextField;
    private javax.swing.JLabel verifyLabel;
    private javax.swing.JButton verifyOKButton;
    private javax.swing.JPanel verifyPanel;
    private javax.swing.JPanel versionInfoPanel;
    private javax.swing.JScrollPane versionInfoScrollPane;
    private javax.swing.JTable versionInfoTable;
    private javax.swing.JCheckBox whitecaneCheckBox;
    private javax.swing.JCheckBox yellowcaneCheckBox;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private JDialog preferencesBox;
    private DefaultMutableTreeNode rootPinTree;
    private DefaultMutableTreeNode rootCertlistTree;
    private DynamicLengthTableModel versionTableModel;
    
    
    // Contains the eID
    private BeID fBEID;
    
    // Contains the virtual eID containing easily accessible data
    private VirtualBEID fVirtualBEID;
    
    // Contains the preferences
    private boolean fVerifyOCSP = false;
    private boolean fVerifyCRL = false;
}
