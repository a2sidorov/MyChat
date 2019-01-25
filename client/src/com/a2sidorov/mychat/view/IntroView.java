package com.a2sidorov.mychat.view;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

public class ConnectionView {

    private JFrame frame;
    private JLabel labelAddress;
    private JLabel labelPort;
    private JLabel labelNickname;
    private JTextField textFieldAddress;
    private JTextField textFieldPort;
    private JTextField textFieldNickname;
    private JButton connectButton;

        public ConnectionView(Properties properties) {
            frame = new JFrame();
            frame.setTitle("MyChat");
            frame.setSize(600,500);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            /* Create UI emements */
            labelAddress = new JLabel("IP Address: ");
            labelPort = new JLabel("Port");
            labelNickname = new JLabel("Nickname");
            textFieldAddress = new JTextField(12);
            textFieldAddress.setText(properties.getProperty("address"));
            textFieldPort = new JTextField(12);
            textFieldPort.setText(properties.getProperty("port"));
            textFieldNickname = new JTextField(12);
            textFieldNickname.setText(properties.getProperty("nickname"));
            connectButton = new JButton("Connect");

            /* Add UI elements to frame */
            Box box = new Box(BoxLayout.Y_AXIS);
            JPanel panelForm = new JPanel(new GridBagLayout());
            Dimension dimension = new Dimension(300, 200);
            panelForm.setPreferredSize(dimension);
            panelForm.setMaximumSize(dimension);

            GridBagConstraints c = new GridBagConstraints();

            // IP Address label
            c.gridx = 0;
            c.gridy = 0;
            panelForm.add(labelAddress, c);

            // Port label
            c.gridy++;
            c.anchor = GridBagConstraints.LINE_END;
            panelForm.add(labelPort, c);

            // Nickname label
            c.gridy++;
            panelForm.add(labelNickname, c);

            // IP Address field
            c.gridx = 1;
            c.gridy = 0;
            c.gridwidth = 2;
            panelForm.add(textFieldAddress, c);

            // Port field
            c.gridy++;
            c.gridwidth = 2;
            panelForm.add(textFieldPort, c);

            // Nickname field
            c.gridy++;
            c.gridwidth = 2;
            panelForm.add(textFieldNickname, c);

            // Connect button
            c.gridy++;
            c.gridwidth = 1;
            c.insets = new Insets(20,0,0,0);
            panelForm.add(connectButton, c);

            box.add(Box.createVerticalGlue());
            box.add(panelForm);
            box.add(Box.createVerticalGlue());
            frame.getContentPane().add(box);




            /*
            JPanel panelMain = new JPanel();
            panelMain.setLayout(new GridBagLayout());
            frame.getContentPane().add(panelMain);

            JPanel dialogPanel = new JPanel();
            dialogPanel.setSize(new Dimension(200, 200));
            mainPanel.add(dialogPanel, BorderLayout.CENTER);

            JLabel labelDialog = new JLabel();
            labelDialog.setText("Enter a nickname:");
            dialogPanel.add(labelDialog);

            JTextField fieldDialog = new JTextField();
            fieldDialog.setPreferredSize(new Dimension(200, 20));
            dialogPanel.add(fieldDialog);

            JButton buttonDialog = new JButton("Save");
            */


        }

}
