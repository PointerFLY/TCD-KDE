import javax.swing.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class QueryWindow extends JFrame {

    QueryHub hub;

    public QueryWindow() {
        super();
        hub = new QueryHub();
        setupUI();
    }

    public void launch() {
        this.setVisible(true);
    }


    private String query(String question) {
        //query here
        return question;
    }

    private void setupUI() {
        int windowWidth = 800;
        int windowHeight = 600;
        int margin = 20;

        String[] questions = new String[20];
        for (int i = 0; i < 20; i++) {
            questions[i] = "question" + i;
        }

        JList questionList = new JList<>(questions);
        questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionList.setVisibleRowCount(0);
        questionList.setSelectedIndex(0);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(questionList);
        scrollPane.setBounds(margin, margin, windowWidth - margin * 4 - 100, 200);

        JButton executeButton = new JButton();
        executeButton.setText("Execute");
        executeButton.setBounds(670, 100, 100, 50);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        JScrollPane resultPane = new JScrollPane();
        resultPane.setViewportView(resultTextArea);
        resultPane.setBounds(margin, 200 + margin * 2, windowWidth - margin * 2, windowHeight - 200 - margin * 4);

        executeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Object i = questionList.getSelectedValue();
                String question = i.toString();
                resultTextArea.setText(query(question));
            }
        });

        this.setTitle("Group 7 Query Graphic User Interface");
        this.getContentPane().setLayout(null);
        this.setBounds(100, 100, windowWidth, windowHeight);
        this.setResizable(false);
        this.add(scrollPane);
        this.add(executeButton);
        this.add(resultPane);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
