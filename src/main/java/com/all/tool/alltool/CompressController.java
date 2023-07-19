package com.all.tool.alltool;

import com.all.tool.alltool.consts.ImageType;
import com.all.tool.alltool.consts.ProcessStatus;
import com.all.tool.alltool.model.FileInfo;
import com.all.tool.alltool.model.ImageRecord;
import com.all.tool.alltool.utils.FileUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CompressController implements Initializable {

    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final int MAX_FILES = 500;

    private static final int MAX_SUBDIRECTORIES = 500;

    private boolean isCompressing = false;

    @FXML
    private Button beginCompress;

    @FXML
    private TableView<ImageRecord> tableView;

    @FXML
    private Button clearButton;

    @FXML
    private HBox successAndFailedBox;

    @FXML
    private Label successNumLabel;

    @FXML
    private Label failedNumLabel;

    @FXML
    private Label imageNumLabel;

    @FXML
    private Label processStatusTip;

    private final List<String> fieldNameList = Arrays.asList(
            "selected",
            "fileName",
            "filePath",
            "showResult",
            "fileShowSize",
            "compressedSize",
            "spaceSaved",
            "spaceSavedPercent");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTableView();
        this.beginCompress.setDisable(true);
        this.successAndFailedBox.setVisible(false);
    }

    @FXML
    public void onBeginCompress() {
        if (isCompressing) {
            return;
        }
        isCompressing = true;
        executor.execute(() -> {
            this.tableView.setEditable(false);
            this.processStatusTip.setVisible(true);
            this.successAndFailedBox.setVisible(true);
            ObservableList<ImageRecord> itemList = tableView.getItems();
            int successNum = 0;
            int failedNum = 0;
            for (ImageRecord record : itemList) {
                if (!record.getSelected()) {
                    continue;
                }
                record.setSelected(false);
                try {
                    String filePath = record.getFilePath();
                    ProcessBuilder pb = null;
                    // 创建一个ProcessBuilder对象
                    if (record.getImageType() == ImageType.PNG) {
                        pb = new ProcessBuilder("src/main/resources/cmd/pngquant.exe", "--quality=10-100", "--force", "--ext=.png", "--skip-if-larger", filePath);
                    } else if (record.getImageType() == ImageType.JPG || record.getImageType() == ImageType.JPEG) {
                        pb = new ProcessBuilder("src/main/resources/cmd/jpegoptim.exe", "--max=50", filePath);
                    } else {
                        continue;
                    }

                    // 启动一个新的进程，并在该进程中运行pngquant命令
                    Process process = null;
                    process = pb.start();
                    // 等待进程执行完毕
                    int exitCode = process.waitFor();
                    System.out.printf("exit code: %d\n", exitCode);
                    record.setResult(exitCode == 0);
                    if (record.getResult()) {
                        successNum++;
                        FileInfo fileInfo = FileUtils.getFileInfo(filePath);
                        record.setCompressedSize(FileUtils.formatFileSize(fileInfo.getSize()));
                        record.setSpaceSaved(FileUtils.formatFileSize(record.getFileSize() - fileInfo.getSize()));
                        record.setSpaceSavedPercent(formatPercent(record.getFileSize(), fileInfo.getSize()));
                    } else {
                        failedNum++;
                    }
                    updateStatusLabel(successNum, failedNum);
                } catch (IOException | InterruptedException e) {
                    record.setResult(false);
                }
                tableView.refresh();
            }
            this.tableView.setEditable(true);
            this.processStatusTip.setVisible(false);
            isCompressing = false;
        });


    }

    public void updateStatusLabel(int successNum, int failedNum) {
        Platform.runLater(() -> {
            this.successNumLabel.setText(String.valueOf(successNum));
            this.failedNumLabel.setText(String.valueOf(failedNum));
            this.processStatusTip.setText("处理中" + ".".repeat((successNum + failedNum) % 4));
        });
    }

    public String formatPercent(long beingSize, long afterSize) {
        return String.format("%.2f", (beingSize - afterSize) * 100.0 / beingSize) + "%";
    }

    /**
     * 点击添加文件按钮
     */
    @FXML
    public void onAddFiles() {
        // 创建一个文件选择器
        FileChooser fileChooser = new FileChooser();

        // 设置文件选择器的标题
        fileChooser.setTitle("选择文件");

        // 添加文件类型过滤器，只允许选择jpg、png文件或文件夹
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("图片文件", "*.jpg", "*.png", "*.jpeg")
        );

        // 允许多选
//        fileChooser.setMultiSelectionEnabled(true);

        // 显示文件选择器并等待用户选择文件
        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(new Stage());

        // 处理用户选择的文件
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                addTableLine(FileUtils.getFileInfo(file));
            }
        }
    }

    @FXML
    public void onAddDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(new Stage());
        if (selectedDirectory == null) {
            return;
        }
        String directoryPath = selectedDirectory.getAbsolutePath();
        File directory = new File(directoryPath);
        List<File> imageFiles = new ArrayList<>();
        int fileCount = 0;
        int subdirectoryCount = 0;

        if (directory.exists() && directory.isDirectory()) {
            processDirectory(directory, imageFiles, fileCount, subdirectoryCount);
        } else {
            System.out.println("指定的路径不是一个有效的目录。");
        }

        for (File file : imageFiles) {
            addTableLine(FileUtils.getFileInfo(file));
        }

    }

    private static void processDirectory(File directory, List<File> imageFiles, int fileCount, int subdirectoryCount) {
        if (subdirectoryCount >= MAX_SUBDIRECTORIES || fileCount >= MAX_FILES) {
            return;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, imageFiles, fileCount, subdirectoryCount + 1);
            } else if (isImageFile(file)) {
                imageFiles.add(file);
                fileCount++;
                if (fileCount >= MAX_FILES) {
                    return;
                }
            }
        }
    }

    private static boolean isImageFile(File file) {
        return (file.getName().endsWith(".png") || file.getName().endsWith(".jpg"));
    }

    private void addTableLine(FileInfo fileInfo) {
        if (fileInfo == null) {
            return;
        }
        ImageRecord record = new ImageRecord();
        record.setSelected(true);
        record.setFileName(fileInfo.getName());
        record.setFilePath(fileInfo.getPath());
        record.setResult(null);
        record.setFileShowSize(fileInfo.getShowSize());
        record.setFileSize(fileInfo.getSize());
        record.setCompressedSize(null);
        record.setSpaceSaved(null);
        record.setSpaceSavedPercent(null);
        record.setImageType(fileInfo.getImageType());
        record.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                updateCompressButtonStatus();
                updateImageNumLabel();
            }
        });
        tableView.getItems().add(record);
    }

    public void updateCompressButtonStatus() {
        boolean allUnselected = true;
        ObservableList<ImageRecord> itemList = this.tableView.getItems();
        for (ImageRecord imageRecord : itemList) {
            if (imageRecord.getSelected()) {
                allUnselected = false;
                break;
            }
        }
        this.beginCompress.setDisable(allUnselected);
    }

    public void initTableView() {
        ObservableList<TableColumn<ImageRecord, ?>> columns = tableView.getColumns();
        TableColumn<ImageRecord, Boolean> firstCheckColumn = (TableColumn<ImageRecord, Boolean>) columns.get(0);
        firstCheckColumn.setCellValueFactory(new PropertyValueFactory<>(fieldNameList.get(0)));
        firstCheckColumn.setCellFactory(tc -> new CheckBoxTableCell<>());
        TableColumn<ImageRecord, String> imageRecordTableColumn = (TableColumn<ImageRecord, String>) columns.get(3);
        imageRecordTableColumn.setCellFactory(new Callback<>() {

            @Override
            public TableCell<ImageRecord, String> call(TableColumn tableColumn) {
                return new TableCell<>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setText(null);
                            setTextFill(Color.BLACK);
                        } else {
                            setText(item);
                            setTextFill(ProcessStatus.SUCCESS.getDesc().equals(item) ? Color.GREEN : Color.RED);
                        }
                    }
                };
            }
        });

        for (int i = 1; i < columns.size(); i++) {
            columns.get(i).setCellValueFactory(new PropertyValueFactory<>(fieldNameList.get(i)));
        }

        this.tableView.setPlaceholder(new Label("请点击添加图片按钮添加图片，或者拖入图片到此处，支持png、jpg格式"));
        this.tableView.getItems().addListener((ListChangeListener<ImageRecord>) change -> {
            updateCompressButtonStatus();
            updateImageNumLabel();
            ;
        });
    }

    private void updateImageNumLabel() {
        ObservableList<ImageRecord> items = this.tableView.getItems();
        int selectedCount;
        int totalCount = items.size();
        selectedCount = (int) items.stream().filter(ImageRecord::getSelected).count();
        Platform.runLater(() -> {
            this.imageNumLabel.setText(String.format("共%d张图片，已选%d张", totalCount, selectedCount));
        });
    }

    @FXML
    public void onRemove() {
        this.tableView.getSelectionModel().clearSelection();
        System.out.printf("onRemove, %s", this.tableView.getSelectionModel());
        System.out.printf("onRemove, %s", this.tableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    public void onRemoveAll() {
        this.tableView.getItems().clear();
    }

    @FXML
    public void onTableViewClicked() {
        System.out.println("onTableViewClicked");
    }

    @FXML
    public void selectAll(ActionEvent event) {
        Object source = event.getSource();
        if (source instanceof CheckBox checkBox) {
            boolean selected = checkBox.isSelected();
            ObservableList<ImageRecord> items = this.tableView.getItems();
            for (ImageRecord item : items) {
                item.setSelected(selected);
            }
        }
    }
}
