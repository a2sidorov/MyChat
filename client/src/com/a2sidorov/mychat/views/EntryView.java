package com.a2sidorov.mychat.views;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class EntryView {

        public static void createEntryView() {



            JPanel panelForm = new JPanel(new GridBagLayout());

            Dimension dimension = new Dimension(300, 200);
            panelForm.setPreferredSize(dimension);
            panelForm.setMaximumSize(dimension);
            panelForm.setBorder(BorderFactory.createTitledBorder("Configuration"));

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 0;
            c.gridy = 0;

            panelForm.add(new JLabel("IP Address: "), c);
            c.gridy++;
            panelForm.add(new JLabel("Port: "), c);
            c.gridy++;
            panelForm.add(new JLabel("Nickanme: "), c);
            c.gridy++;


            c.gridx = 1;
            c.gridy = 0;
            panelForm.add(new JTextField(12), c);
            c.gridy++;
            panelForm.add(new JTextField(12), c);
            c.gridy++;
            panelForm.add(new JTextField(12), c);
            c.gridy += 2;
            panelForm.add(new JButton("Connect"), c);




            Box box = new Box(BoxLayout.Y_AXIS);
            box.add(Box.createVerticalGlue());
            box.add(panelForm);
            box.add(Box.createVerticalGlue());


            JFrame frame = new JFrame();
            frame.setTitle("MyChat");
            frame.setSize(600,500);
            frame.setLocationRelativeTo(null);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
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
