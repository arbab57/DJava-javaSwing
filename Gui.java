import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class ToolButton extends JButton {
    public ToolButton(String text, Color bg, Color fg, Runnable action) {
        super(text);
        this.setBackground(bg);
        this.setForeground(fg);
        this.setFont(new Font("Serif", 0, 16));
        this.setFocusPainted(false);
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}

class Tab extends JButton {
    public int index = -1;
    public Tab(String text, Color bg, Color fg, int index,Runnable action) {
        super(text);
        this.index = index;
        this.setBackground(bg);
        this.setForeground(fg);
        this.setFont(new Font("Serif", 0, 16));
        this.setFocusPainted(false);
        this.setPreferredSize(new Dimension(110, 25));
        this.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.run();
            }
        });
    }
}

public class Gui {
    private static int fontSize = 20;
    private static Logic logic = new Logic();
    private static Color tabBgColor = new Color(225,225,225);
    private static TextArea textArea = new TextArea();
    private static JPanel tabPanal = new JPanel();
        


    public static void init() {
        JFrame frame = new JFrame("DJAVA");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(new Color(236, 236,236));
        frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), 1));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int  screenWidth = (int)screenSize.getWidth();
        int  screenHeight = (int)screenSize.getHeight();

        Color btnBgColor = new Color(55, 136, 216);
        Color btnFgColor = Color.BLACK;

        JPanel toolPanal = new JPanel();
        toolPanal.setMaximumSize(new Dimension(screenWidth, 50));
        toolPanal.setLayout(new FlowLayout(FlowLayout.LEFT));


        tabPanal.setMaximumSize(new Dimension(screenWidth, 25));
        tabPanal.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        updateTabs(tabPanal);


        
        JPanel textPanal = new JPanel();
        textPanal.add(tabPanal);
        textPanal.setLayout(new BoxLayout(textPanal, BoxLayout.Y_AXIS));

        textArea.setFont(new Font("Serif", 0, fontSize));
        textArea.setPreferredSize(new Dimension(screenWidth, screenHeight));
        textPanal.add(textArea);
        
        ToolButton openBtn = new ToolButton("New", btnBgColor, btnFgColor, () -> {
            logic.choosefile();
            updateUI(tabPanal, textArea);
        });
        ToolButton openNewBtn = new ToolButton("Open File", btnBgColor, btnFgColor, () -> {
            logic.open();
            updateTabs(tabPanal);
            setTextInTextArea(textArea);
        });
        ToolButton saveBtn = new ToolButton("Save", btnBgColor, btnFgColor,() -> {
            logic.saveCurrentFile(textArea);
        });
        ToolButton closeBtn = new ToolButton("Close", btnBgColor, btnFgColor,() -> {
            logic.closeCurrentFile(textArea);
            updateUI(tabPanal, textArea);
        });
        ToolButton runAndCompileBtn = new ToolButton("Compile & Run", btnBgColor, btnFgColor, () -> {
            logic.runJavaFile(textArea);
        });
        
        
        JPanel tPanalLeft = new JPanel();
        tPanalLeft.add(openBtn);
        tPanalLeft.add(openNewBtn);
        tPanalLeft.add(saveBtn);
        tPanalLeft.add(closeBtn);
        tPanalLeft.add(runAndCompileBtn);
        toolPanal.add(tPanalLeft);
        
        toolPanal.add(Box.createHorizontalStrut(screenWidth-550));
        
        
        String[] options = {"14", "16", "18", "20", "22", "24", "26", "28", "30", "32", "34", "36", "40"};
        JComboBox<String> comboBox = new JComboBox<>(options);
        toolPanal.add(comboBox);
        


        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedOption = Integer.parseInt((String) comboBox.getSelectedItem());
                fontSize = selectedOption;
                textArea.setFont(new Font("Serif", 0, fontSize));
                textArea.repaint();
                textArea.revalidate();
            }
        });


        frame.add(toolPanal);
        frame.add(textPanal);
        frame.setVisible(true);
    }

    public static void updateUI(JPanel tabPanel,TextArea textArea) {
        updateTabs(tabPanel);
        updateTextArea(textArea);
    }

    public static void changeFile(int i) {
        logic.saveCurrentFile(textArea);
        logic.setCurrentIndex(i);
        updateTabs(tabPanal);
        setTextInTextArea(textArea);
    }
    
    public static void updateTabs(JPanel tabPanel) {
        tabPanel.removeAll();
        if(logic.getFilesArray().size() == 0){
            Tab tab = new Tab("No File", tabBgColor, Color.BLACK, -1,() -> {});
            tabPanel.add(tab);
        }else{
            for (int i = 0; i < logic.getFilesArray().size(); i++) {
                final int index = i; 
                if(i == logic.getCurrentFileIndex()){
                    Tab tab = new Tab(logic.getFilesArray().get(i).name, Color.BLUE, Color.BLACK, i,() -> {
                        changeFile(index);
                    });
                    tabPanel.add(tab);
                    continue;
                }
                Tab tab = new Tab(logic.getFilesArray().get(i).name, tabBgColor, Color.BLACK, i,() -> {
                    changeFile(index);
                });
                tabPanel.add(tab);
            }
        }
        tabPanel.repaint();
        tabPanel.revalidate();
    }

    public static void updateTextArea(TextArea textArea) {
        textArea.setText("");
        textArea.repaint();
        textArea.revalidate();
    }
    public static void setTextInTextArea(TextArea textArea) {
        if (logic.getCurrentFileIndex() < 0 || logic.getCurrentFileIndex() >= logic.getFilesArray().size()) {
            System.out.println("No file selected for saving.");
            return;
        }
        StringBuilder data = logic.readFile(logic.getFilesArray().get(logic.getCurrentFileIndex()).file);
        textArea.setText(data.toString());
        textArea.repaint();
        textArea.revalidate();
    }

}
