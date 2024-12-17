package com.nawinds.hotkeys_reference;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Objects;

public class HotKeysDialog extends DialogWrapper {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;

    private final Object[][] hotkeysData = {
            // Editing Shortcuts
            {"Copy", "Ctrl + C", "Editing"},
            {"Paste", "Ctrl + V", "Editing"},
            {"Undo", "Ctrl + Z", "Editing"},
            {"Redo", "Ctrl + Shift + Z", "Editing"},
            {"Cut", "Ctrl + X", "Editing"},
            {"Duplicate Line", "Ctrl + D", "Editing"},
            {"Delete Line", "Ctrl + Y", "Editing"},
            {"Join Lines", "Ctrl + Shift + J", "Editing"},
            {"Move Line Up", "Alt + Shift + Up", "Editing"},
            {"Move Line Down", "Alt + Shift + Down", "Editing"},
            {"Reformat Code", "Ctrl + Alt + L", "Editing"},
            {"Optimize Imports", "Ctrl + Alt + O", "Editing"},
            {"Comment/Uncomment Line", "Ctrl + /", "Editing"},
            {"Block Comment/Uncomment", "Ctrl + Shift + /", "Editing"},
            {"Select Word", "Ctrl + W", "Editing"},
            {"Expand Selection", "Ctrl + W", "Editing"},
            {"Shrink Selection", "Ctrl + Shift + W", "Editing"},
            {"Surround with Code Block", "Ctrl + Alt + T", "Editing"},
            {"Basic Code Completion", "Ctrl + Space", "Editing"},
            {"Smart Code Completion", "Ctrl + Shift + Space", "Editing"},
            {"Show Quick Fixes", "Alt + Enter", "Editing"},

            // Navigation Shortcuts
            {"Search Everywhere", "Shift, Shift", "Navigation"},
            {"Search File", "Ctrl + Shift + N", "Navigation"},
            {"Navigate to Class", "Ctrl + N", "Navigation"},
            {"Navigate to Symbol", "Ctrl + Shift + Alt + N", "Navigation"},
            {"Navigate to Line", "Ctrl + G", "Navigation"},
            {"Navigate Back", "Ctrl + Alt + Left", "Navigation"},
            {"Navigate Forward", "Ctrl + Alt + Right", "Navigation"},
            {"Navigate to Recent Files", "Ctrl + E", "Navigation"},
            {"Navigate to Related Symbol", "Ctrl + Alt + Home", "Navigation"},
            {"Navigate to Superclass", "Ctrl + U", "Navigation"},
            {"Navigate to Implementation", "Ctrl + Alt + B", "Navigation"},
            {"Open File Structure", "Ctrl + F12", "Navigation"},

            // Search Shortcuts
            {"Find", "Ctrl + F", "Search"},
            {"Replace", "Ctrl + R", "Search"},
            {"Find in Files", "Ctrl + Shift + F", "Search"},
            {"Replace in Files", "Ctrl + Shift + R", "Search"},
            {"Find Next", "F3", "Search"},
            {"Find Previous", "Shift + F3", "Search"},
            {"Highlight Usages in File", "Ctrl + Shift + F7", "Search"},
            {"Show Usages", "Ctrl + Alt + F7", "Search"},

            // Refactoring Shortcuts
            {"Rename", "Shift + F6", "Refactoring"},
            {"Extract Variable", "Ctrl + Alt + V", "Refactoring"},
            {"Extract Method", "Ctrl + Alt + M", "Refactoring"},
            {"Extract Field", "Ctrl + Alt + F", "Refactoring"},
            {"Extract Constant", "Ctrl + Alt + C", "Refactoring"},
            {"Inline Variable/Method", "Ctrl + Alt + N", "Refactoring"},
            {"Safe Delete", "Alt + Delete", "Refactoring"},
            {"Move File/Element", "F6", "Refactoring"},

            // Debugging Shortcuts
            {"Debug", "Shift + F9", "Debugging"},
            {"Step Over", "F8", "Debugging"},
            {"Step Into", "F7", "Debugging"},
            {"Step Out", "Shift + F8", "Debugging"},
            {"Run to Cursor", "Alt + F9", "Debugging"},
            {"Evaluate Expression", "Alt + F8", "Debugging"},
            {"Toggle Breakpoint", "Ctrl + F8", "Debugging"},
            {"View Breakpoints", "Ctrl + Shift + F8", "Debugging"},
            {"Resume Program", "F9", "Debugging"},
            {"Stop Program", "Ctrl + F2", "Debugging"},

            // Version Control Shortcuts
            {"Commit Changes", "Ctrl + K", "Version Control"},
            {"Update Project", "Ctrl + T", "Version Control"},
            {"Push Changes", "Ctrl + Shift + K", "Version Control"},
            {"Rollback Changes", "Ctrl + Alt + Z", "Version Control"},
            {"Show Changes", "Ctrl + D", "Version Control"},
            {"Show Log", "Alt + 9", "Version Control"},
            {"Next Change", "Ctrl + Shift + Down", "Version Control"},
            {"Previous Change", "Ctrl + Shift + Up", "Version Control"},

            // General Shortcuts
            {"Open Settings", "Ctrl + Alt + S", "General"},
            {"Search Actions", "Ctrl + Shift + A", "General"},
            {"Open Terminal", "Alt + F12", "General"},
            {"Hide All Tool Windows", "Ctrl + Shift + F12", "General"},
            {"Open Recent Projects", "Ctrl + Shift + E", "General"},
            {"Save All", "Ctrl + S", "General"},
            {"Exit", "Ctrl + Q", "General"}
    };

    private static final boolean IS_MAC = System.getProperty("os.name").toLowerCase().contains("mac");

    private void adjustShortcutsForMac() {
        for (int i = 0; i < hotkeysData.length; i++) {
            hotkeysData[i][1] = hotkeysData[i][1].toString().replace("Ctrl", "⌘");
        }
    }

    public HotKeysDialog() {
        super(true); // use current window as parent
        init();
        setTitle("Hotkeys Reference");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Create the search field
        searchField = new JTextField();
        searchField.setToolTipText("Search for a hotkey...");
        searchField.setPreferredSize(new Dimension(400, 30));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        addPlaceholder(searchField);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterTable();
            }
        });

        // Category Filter
        categoryFilter = new ComboBox<>(new String[]{"All", "Editing", "Navigation", "Search"});
        categoryFilter.setPreferredSize(new Dimension(150, 30));
        categoryFilter.addActionListener(e -> filterTable());

        // Add filters to panel
        filterPanel.add(searchField);
        filterPanel.add(categoryFilter);

        // Table
        String[] columns = {"Action", "Shortcut", "Category"};
        if (IS_MAC) {
            adjustShortcutsForMac();
        }
        model = new DefaultTableModel(hotkeysData, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make the table read-only
            }
        };

        table = new JBTable(model);

        // Customize the appearance of the "Shortcut" column
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        renderer.setFont(new Font("Monospaced", Font.BOLD, 12));
        renderer.setForeground(JBColor.YELLOW); // Highlight shortcuts in blue
        table.getColumnModel().getColumn(1).setCellRenderer(renderer);

        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        // Add the search field and the table to the panel
        mainPanel.add(searchField, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private void addPlaceholder(JTextField textField) {
        Color defaultTextColor = textField.getForeground();

        textField.setForeground(JBColor.GRAY);
        textField.setText("Search for a hotkey...");

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals("Search for a hotkey...")) {
                    textField.setText("");
                    textField.setForeground(defaultTextColor);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(JBColor.GRAY);
                    textField.setText("Search for a hotkey...");
                }
            }
        });
    }

    private void filterTable() {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        String searchQuery = searchField.getText().trim();
        String selectedCategory = Objects.requireNonNull(categoryFilter.getSelectedItem()).toString();

        RowFilter<DefaultTableModel, Object> searchFilter = searchQuery.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + searchQuery);

        RowFilter<DefaultTableModel, Object> categoryFilter = selectedCategory.equals("All")
                ? null
                : RowFilter.regexFilter("^" + selectedCategory + "$", 2); // Apply to the "Category" column (index 2)

        // Combine filters (if both are applied)
        if (searchFilter != null && categoryFilter != null) {
            sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(searchFilter, categoryFilter)));
        } else // Show all rows
            if (searchFilter != null) {
                sorter.setRowFilter(searchFilter);
            } else sorter.setRowFilter(categoryFilter);
    }

}
