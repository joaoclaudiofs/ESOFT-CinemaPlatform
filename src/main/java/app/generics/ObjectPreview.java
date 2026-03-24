package app.generics;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ObjectPreview extends JFrame {
    private JPanel panelRoot;

    private JPanel panelHeader;
    private JButton buttonBack;
    private JLabel labelWindowTitle;

    private JScrollPane panelScroll;

    private JPanel panelFooter;
    private JLabel labelInfo;
    private JButton buttonSave;
    private JButton buttonCancel;
    private JButton buttonDelete;

    protected PreviewType previewType = PreviewType.UNDEFINED;
    protected Object object = null;

    public ObjectPreview(String title, Button ...buttons) {
        this(title, title, buttons);
    }

    public ObjectPreview(String windowTitle, String pageTitle, Button ...buttons) {
        super();
        this.setTitle(windowTitle);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setSize(400, 400);
        this.setMinimumSize(new Dimension(300, 300));
        this.setLocationRelativeTo(null);
        this.setContentPane(panelRoot);

        labelWindowTitle.setText(pageTitle);

        buttonBack.setIcon(new FlatSVGIcon("icons/arrows/chevron-left.svg", 24, 24));
        buttonBack.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
        buttonBack.addActionListener(this::onBack);
        buttonBack.setVisible(false);


        panelScroll.putClientProperty(JScrollBar.WIDTH, 16);
        panelScroll.putClientProperty(FlatClientProperties.SCROLL_PANE_SMOOTH_SCROLLING, true);
        panelScroll.putClientProperty(FlatClientProperties.SCROLL_BAR_SHOW_BUTTONS, true);
        panelScroll.getVerticalScrollBar().setUnitIncrement(16);

        labelInfo.setText(null);
        labelInfo.setToolTipText(null);

        buttonSave.addActionListener(this::onSave);
        buttonSave.setVisible(false);

        buttonCancel.addActionListener(this::onCancel);
        buttonCancel.setVisible(false);

        buttonDelete.addActionListener(this::onDelete);
        buttonDelete.setVisible(false);

        for (Button button : buttons) {
            if (button == null) continue;
            setButtonVisible(button, true);
        }
    }

    public void setWindowTitle(String title) {
        this.setTitle(title);
    }

    public void setMainPanel(JPanel panel) {
        panelScroll.setViewportView(panel);
        panelScroll.revalidate();
        panelScroll.repaint();
    }

    public void setFooterInfo(String info) {
        labelInfo.setText(info);
        labelInfo.setToolTipText(info);
    }

    public void setButtonVisible(Button button, boolean visible) {
        switch (button) {
            case SAVE: buttonSave.setVisible(visible);
                break;
            case CANCEL: buttonCancel.setVisible(visible);
                break;
            case DELETE: buttonDelete.setVisible(visible);
                break;
            case BACK: buttonBack.setVisible(visible);
                break;
        }
    }

    public void setButtonEnabled(Button button, boolean enabled) {
        switch (button) {
            case SAVE: buttonSave.setEnabled(enabled);
                break;
            case CANCEL: buttonCancel.setEnabled(enabled);
                break;
            case DELETE: buttonDelete.setEnabled(enabled);
                break;
            case BACK: buttonBack.setEnabled(enabled);
                break;
        }
    }

    public PreviewType getPreviewType() {
        return previewType;
    }

    public Object getObject() {
        return object;
    }

    protected enum Button {
        SAVE(1),
        CANCEL(2),
        DELETE(3),
        BACK(4);

        private final int buttonId;
        Button (int buttonId) {
            this.buttonId = buttonId;
        }

        public int getValue() {
            return buttonId;
        }
    }

    public enum PreviewType {
        UNDEFINED(0),
        VIEW(1),
        EDIT(2),
        CREATE(3);

        private final int typeId;
        PreviewType(int typeId) {
            this.typeId = typeId;
        }

        public int getValue() {
            return typeId;
        }
    }

    public void onSave(ActionEvent ev) {
        // overridable
    }

    public void onCancel(ActionEvent ev) {
        // overridable
    }

    public void onDelete(ActionEvent ev) {
        // overridable
    }

    public void onBack(ActionEvent ev) {
        // overridable
        this.dispose();
    }

}
