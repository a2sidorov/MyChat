package com.a2sidorov.mychat;

import com.a2sidorov.mychat.controller.IntroController;
import com.a2sidorov.mychat.controller.MainController;
import com.a2sidorov.mychat.model.Settings;
import com.a2sidorov.mychat.view.IntroView;
import com.a2sidorov.mychat.view.MainView;
import com.a2sidorov.mychat.view.Notification;

import javax.swing.*;

/**
 * The MyChat MyChatClient.
 *
 * @author Andrei Sidorov
 * @version 1.0
 * @since 2018.11.09
 */

public class MyChatClient {

    private static JFrame initFrame() {
        JFrame frame = new JFrame();
        frame.setTitle("MyChat");
        frame.setSize(600,500);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = initFrame();
            Notification notification = new Notification();

            // Intro MVC
            Settings settings = new Settings(notification);
            IntroView introView = new IntroView(frame);
            IntroController introController = new IntroController(settings, introView);

            // Main MVC
            MainView mainView = new MainView(frame);
            MainController mainController = new MainController(settings, mainView);

            // Giving controllers access to each other for transitions between views
            introController.setMainController(mainController);
            mainController.setIntroController(introController);

            // Display intro view
            introController.initView();
            introController.initController();
        });
    }

}
