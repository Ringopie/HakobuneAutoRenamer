package imgdir;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
/**
 * 1つの画像のプレビューパネルを定義します。
 */
public class FilePreviewPanel extends JPanel {

    private BufferedImage image;
    private JTextField textField;
    private JCheckBox useCheckBox;
    private JCheckBox hardCheckBox;

    public FilePreviewPanel(String fileName, String text, BufferedImage image) {
        this.image = image;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel nameLabel = new JLabel("File: " + fileName);
        add(nameLabel, BorderLayout.NORTH);

        if (image != null) {
            JLabel imageLabel = new JLabel(new ImageIcon(image.getScaledInstance(200, -1, Image.SCALE_SMOOTH)));
            add(imageLabel, BorderLayout.CENTER);
        }

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));

        textField = new JTextField(text);
        textField.setMaximumSize(new Dimension(200, 30));
        textPanel.add(textField);

        useCheckBox = new JCheckBox("保存対象にする", true);
        textPanel.add(useCheckBox);

        hardCheckBox = new JCheckBox("強襲");
        textPanel.add(hardCheckBox);

        add(textPanel, BorderLayout.SOUTH);
    }

    public BufferedImage getImage() {
        return image;
    }

    public JTextField getTextField() {
        return textField;
    }

    public boolean isSelected() {
        return useCheckBox.isSelected();
    }

    public boolean isHardSelected() {
        return hardCheckBox.isSelected();
    }
}