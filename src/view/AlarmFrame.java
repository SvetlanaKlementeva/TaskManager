package src.view;

import src.controller.Controller;
import src.controller.TaskList;
import src.model.Task;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;

public class AlarmFrame extends JFrame {
    private Task task;
    private TaskList taskList;
    private MainFrame mainFrame;

    private JPanel panelMain;
    private JButton btCompleteTask;
    private JButton btPostponeTask;

    private JRadioButton rb5Minute;
    private JRadioButton rb10Minute;
    private JRadioButton rb30Minute;
    private JRadioButton rbHour;
    private JRadioButton rbDay;
    private JRadioButton rbManually;
    private ButtonGroup radioButtonGroup;

    private JLabel lTaskInfo;
    private JFormattedTextField formattedTextField;

    public AlarmFrame(Task task, TaskList taskList, MainFrame mainFrame) {
        this.task = task;
        this.taskList = taskList;
        this.mainFrame = mainFrame;
        groupRadioButton();
        showTask();

        //Добавляем слушателей на RadioButton
        Enumeration<AbstractButton> buttons = radioButtonGroup.getElements();
        while (buttons.hasMoreElements()) {
            AbstractButton b = buttons.nextElement();
            if (b != rbManually)
                b.addActionListener(event -> {
                    formattedTextField.setEnabled(false);
                });
            else
                b.addActionListener(event -> {
                    formattedTextField.setEnabled(true);
                });
        }

        //Устанавливаем MaskFormatter на FormattedTextField
        MaskFormatter mf = null;
        try {
            mf = new MaskFormatter("##/##/####   ##:##");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mf.setPlaceholderCharacter('_');
        DefaultFormatterFactory dff = new DefaultFormatterFactory(mf);
        formattedTextField.setFormatterFactory(dff);

        //Завершаем Task
        btCompleteTask.addActionListener(event -> {
            taskList.complete(task);
            mainFrame.update();
            dispose();
        });

        //Откладываем Task
        btPostponeTask.addActionListener(event -> {
            boolean isCorrect = true;
            Calendar newDate = Calendar.getInstance();
            int minutes = 0;
            if (rb5Minute.isSelected())
                minutes = 5;
            else if (rb10Minute.isSelected())
                minutes = 10;
            else if (rb30Minute.isSelected())
                minutes = 30;
            else if (rbHour.isSelected())
                minutes = 60;
            else if (rbDay.isSelected())
                minutes = 1440;
            else if (rbManually.isSelected()) {
                try {
                    formattedTextField.commitEdit();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy   HH:mm");
                    sdf.setLenient(false);
                    Date date = sdf.parse((String) formattedTextField.getValue());

                    //Сравниваем введённое время с текущим
                    if (new Date().after(date)) {
                        JOptionPane.showMessageDialog(this,
                                "Невозможно отложить задачу в прошлое!");
                        isCorrect = false;
                    }

                    newDate.setTime(date);
                } catch (ParseException | DateTimeException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage(),
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                    isCorrect = false;
                }
            }
            if (isCorrect) {
                newDate.add(Calendar.MINUTE, minutes);
                taskList.postpone(task, newDate);
                mainFrame.update();
                dispose();
            }
        });

        setContentPane(panelMain);
        setTitle("AlarmFrame");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int sizeWidth = 300;
        int sizeHeight = 400;
        int locationX = (screenSize.width - sizeWidth) / 2;
        int locationY = (screenSize.height - sizeHeight) / 2;
        setBounds(locationX, locationY, sizeWidth, sizeHeight);

        setVisible(true);
    }


    private void groupRadioButton() {
        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(rb5Minute);
        radioButtonGroup.add(rb10Minute);
        radioButtonGroup.add(rb30Minute);
        radioButtonGroup.add(rbHour);
        radioButtonGroup.add(rbDay);
        radioButtonGroup.add(rbManually);
    }

    private void showTask() {
        Calendar dateTime = task.getDateTime();
        Formatter dataTimeString = new Formatter();
        dataTimeString.format("%td/%tm/%tY   %tH:%tM", dateTime, dateTime, dateTime, dateTime, dateTime);

        String text = "<html>"
                + task.getName() + "<br>"
                + task.getInfo() + "<br>"
                + dataTimeString.toString() + "<br><br>"
                + "Отложить на:"
                + "</html>";
        lTaskInfo.setText(text);
    }
}