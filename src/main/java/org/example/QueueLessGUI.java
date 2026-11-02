package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class QueueLessGUI extends JFrame {

    private final Color navy = new Color(25, 45, 85);
    private final Color background = new Color(245, 247, 252);
    private final Color blue = new Color(55, 105, 220);
    private final Color green = new Color(35, 160, 110);
    private final Color red = new Color(210, 65, 65);

    private JLabel totalLabel;
    private JLabel waitingLabel;
    private JLabel servedLabel;
    private JLabel nowServingLabel;

    private final TokenDAO tokenDAO = new TokenDAO();

    public QueueLessGUI() {

        setTitle("QueueLess | Virtual Queue Management");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(background);

        // Header
        JPanel header = new JPanel();
        header.setBackground(navy);
        header.setPreferredSize(new Dimension(750, 110));
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("QueueLess");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Virtual Queue Management System");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(new Color(220, 230, 255));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(Box.createVerticalGlue());
        header.add(title);
        header.add(Box.createRigidArea(new Dimension(0, 5)));
        header.add(subtitle);
        header.add(Box.createVerticalGlue());

        // Dashboard cards
        JPanel dashboard = new JPanel(new GridLayout(1, 4, 10, 10));
        dashboard.setBackground(background);
        dashboard.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        totalLabel = new JLabel("0", SwingConstants.CENTER);
        waitingLabel = new JLabel("0", SwingConstants.CENTER);
        servedLabel = new JLabel("0", SwingConstants.CENTER);
        nowServingLabel = new JLabel("None", SwingConstants.CENTER);

        dashboard.add(createCard("Total Tokens", totalLabel, blue));
        dashboard.add(createCard("Waiting", waitingLabel, red));
        dashboard.add(createCard("Served", servedLabel, green));
        dashboard.add(createCard("Now Serving", nowServingLabel, navy));

        // Action buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonsPanel.setBackground(background);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 70, 30, 70));

        JButton takeTokenButton = createButton("Take a Token", blue);
        JButton viewQueueButton = createButton("View Queue", navy);
        JButton serveNextButton = createButton("Serve Next", green);
        JButton cancelTokenButton = createButton("Cancel Token", red);

        buttonsPanel.add(takeTokenButton);
        buttonsPanel.add(viewQueueButton);
        buttonsPanel.add(serveNextButton);
        buttonsPanel.add(cancelTokenButton);

        // Take Token
        takeTokenButton.addActionListener(e -> {

            String studentName = JOptionPane.showInputDialog(
                    this,
                    "Enter your name:",
                    "Take a Token",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (studentName != null && !studentName.trim().isEmpty()) {

                Token token = tokenDAO.generateToken(studentName.trim());

                if (token != null) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Token generated successfully!\n\n"
                                    + "Token Number: " + token.getTokenNumber()
                                    + "\nName: " + token.getStudentName()
                                    + "\nStatus: " + token.getStatus()
                    );

                    updateDashboard();

                } else {
                    JOptionPane.showMessageDialog(this, "Unable to generate token.");
                }
            }
        });

        // View Queue
        viewQueueButton.addActionListener(e -> {

            ArrayList<Token> tokens = tokenDAO.getAllTokens();

            String[] columns = {"Token Number", "Student Name", "Status"};

            DefaultTableModel model = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Token token : tokens) {

                model.addRow(new Object[]{
                        token.getTokenNumber(),
                        token.getStudentName(),
                        token.getStatus()
                });
            }

            JTable table = new JTable(model);
            table.setRowHeight(25);
            table.setFont(new Font("Arial", Font.PLAIN, 14));
            table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300));

            JOptionPane.showMessageDialog(
                    this,
                    scrollPane,
                    "Current Queue",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });

        // Serve Next
        serveNextButton.addActionListener(e -> {

            boolean served = tokenDAO.serveNextToken();

            if (served) {

                JOptionPane.showMessageDialog(
                        this,
                        "Next waiting token has been served!"
                );

                updateDashboard();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "No waiting tokens available."
                );
            }
        });

        // Cancel Token
        cancelTokenButton.addActionListener(e -> {

            String input = JOptionPane.showInputDialog(
                    this,
                    "Enter the token number to cancel:"
            );

            if (input != null) {

                try {

                    int tokenNumber = Integer.parseInt(input);

                    boolean cancelled = tokenDAO.cancelToken(tokenNumber);

                    if (cancelled) {

                        JOptionPane.showMessageDialog(
                                this,
                                "Token " + tokenNumber + " cancelled successfully!"
                        );

                        updateDashboard();

                    } else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Token not found or it has already been served/cancelled."
                        );
                    }

                } catch (NumberFormatException ex) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a valid token number."
                    );
                }
            }
        });

        // Footer
        JLabel footer = new JLabel(
                "QueueLess | Smart. Simple. Efficient.",
                SwingConstants.CENTER
        );

        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(background);
        centerPanel.add(dashboard, BorderLayout.NORTH);
        centerPanel.add(buttonsPanel, BorderLayout.CENTER);

        mainPanel.add(header, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footer, BorderLayout.SOUTH);

        add(mainPanel);

        updateDashboard();

        setVisible(true);
    }

    // Dashboard card
    private JPanel createCard(String title, JLabel value, Color color) {

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(color, 2));

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 13));
        heading.setForeground(color);

        value.setFont(new Font("Arial", Font.BOLD, 25));
        value.setForeground(navy);

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // Styled button
    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return button;
    }

    // Update dashboard values
    private void updateDashboard() {

        totalLabel.setText(String.valueOf(tokenDAO.getTotalTokens()));
        waitingLabel.setText(String.valueOf(tokenDAO.getWaitingTokens()));
        servedLabel.setText(String.valueOf(tokenDAO.getServedTokens()));

        int nowServing = tokenDAO.getNowServing();

        if (nowServing == 0) {
            nowServingLabel.setText("None");
        } else {
            nowServingLabel.setText(String.valueOf(nowServing));
        }
    }
}