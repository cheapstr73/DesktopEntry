package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import javafx.event.ActionEvent;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    // FXML variables
    @FXML private Label lblExecutable, lblIcon;
    @FXML private ListView listView, listViewAdditional;

    // File chooser and the extension filter
    private FileChooser fileChooser = new FileChooser();
    private FileChooser.ExtensionFilter iconFilter = new FileChooser.ExtensionFilter("Icons", "*.svg", "*.png", "*.ico");

    private File lastSelectedDirectory = new File(System.getProperty("user.home"));

    private File selectedExecutablePath, selectedIconPath;
    private String strExecutableParentPath, strIconParentpath, strExecutableName, strIconName;

    private final String iconDir = System.getProperty("user.home") + "/.local/share/icons/";
    private final String appDir = System.getProperty("user.home") + "/.local/share/applications/";

    public void initialize()
    {
        ObservableList primaryList = FXCollections.observableArrayList(
                "AudioVideo",
                "Development",
                "Education",
                "Game",
                "Graphics",
                "Network",
                "Office",
                "Science",
                "Settings",
                "System",
                "Utility"
        );
        listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listView.setItems(primaryList);

        ObservableList additionalList = FXCollections.observableArrayList(
        "2DGraphics",
        "3DGraphics",
        "Accessibility",
        "ActionGame",
        "Adult",
        "AdventureGame",
        "Amusement",
        "ArcadeGame",
        "Archiving",
        "Art",
        "ArtificialIntelligence",
        "Astronomy",
        "AudioVideoEditing",
        "Biology",
        "BlocksGame",
        "BoardGame",
        "Building",
        "Calculator",
        "Calendar",
        "CardGame",
        "Chart",
        "Chat",
        "Chemistry",
        "Clock",
        "Compression",
        "ComputerScience",
        "ConsoleOnly",
        "Construction",
        "ContactManagement",
        "Core",
        "Database",
        "DataVisualization",
        "Debugger",
        "DesktopSettings",
        "Dialup",
        "Dictionary",
        "DiscBurning",
        "Documentation",
        "Economy",
        "Electricity",
        "Electronics",
        "Email",
        "Emulator",
        "Engineering",
        "Feed",
        "FileManager",
        "Filesystem",
        "FileTools",
        "FileTransfer",
        "Finance",
        "FlowChart",
        "Geography",
        "Geology",
        "Geoscience",
        "GNOME",
        "GTK",
        "GUIDesigner",
        "HamRadio",
        "HardwareSettings",
        "History",
        "Humanities",
        "IDE",
        "ImageProcessing",
        "InstantMessaging",
        "IRCClient",
        "Java",
        "KDE",
        "KidsGame",
        "Languages",
        "Literature",
        "LogicGame",
        "Maps",
        "Math",
        "MedicalSoftware",
        "Midi",
        "Mixer",
        "Monitor",
        "Motif",
        "Music",
        "News",
        "NumericalAnalysis",
        "OCR",
        "P2P",
        "PackageManager",
        "ParallelComputing",
        "PDA",
        "Photography",
        "Physics",
        "Player",
        "Presentation",
        "Printing",
        "Profiling",
        "ProjectManagement",
        "Publishing",
        "Qt",
        "RasterGraphics",
        "Recorder",
        "RemoteAccess",
        "RevisionControl",
        "Robotics",
        "RolePlaying",
        "Scanning",
        "Security",
        "Sequencer",
        "Shooter",
        "Simulation",
        "Spirituality",
        "Sports",
        "SportsGame",
        "Spreadsheet",
        "StrategyGame",
        "Telephony",
        "TelephonyTools",
        "TerminalEmulator",
        "TextEditor",
        "TextTools",
        "Translation",
        "Tuner",
        "TV",
        "VectorGraphics",
        "VideoConference",
        "Viewer",
        "WebBrowser",
        "WebDevelopment",
        "WordProcessor",
        "XFCE"
        );

        listViewAdditional.setItems(additionalList);
    }

    @FXML protected void selectExecutable(ActionEvent e)
    {
        fileChooser.getExtensionFilters().removeAll(iconFilter);
        fileChooser.setInitialDirectory(lastSelectedDirectory);

        selectedExecutablePath = fileChooser.showOpenDialog(null);

        if(selectedExecutablePath == null)
            return;

        lblExecutable.setText(selectedExecutablePath.toString());

        // ...so that next time we open FileChooser we can start in the same location
        lastSelectedDirectory = selectedExecutablePath.getParentFile();

        String strExe = selectedExecutablePath.toString();
        strExecutableParentPath = strExe.substring(0, strExe.lastIndexOf("/") + 1);
        strExecutableName = strExe.substring(strExe.lastIndexOf("/") + 1);
        System.out.println("LastD: " + lastSelectedDirectory.toString());
    }

    @FXML protected void selectIcon(ActionEvent e)
    {
        fileChooser.getExtensionFilters().add(iconFilter);
        fileChooser.setInitialDirectory(lastSelectedDirectory);

        selectedIconPath = fileChooser.showOpenDialog(null);

        if(selectedIconPath == null)
            return;

        // ...so that next time we open FileChooser we can start in the same location
        lastSelectedDirectory = selectedIconPath.getParentFile();

        String strIcon = selectedIconPath.toString();
        lblIcon.setText(strIcon);
        strIconParentpath = strIcon.substring(0, strIcon.lastIndexOf("/") + 1);
        strIconName = strIcon.substring(strIcon.lastIndexOf("/") + 1);

        System.out.println(strIconParentpath + "\n" + strIconName);

    }

    @FXML protected void createEntry()
    {
            if(!copyIcon())
                return;

        try
        {
            File f = new File(appDir + strExecutableName + ".desktop");
            System.out.println("Creating desktop file: " + f.toString());

            FileWriter fw = new FileWriter(f);

            StringBuilder sb = new StringBuilder();
            sb.append("[Desktop Entry]\n");
            sb.append("Encoding=UTF-8\n");
            sb.append("Version=1.0\n");
            sb.append("Type=Application\n");
            sb.append("Terminal=false\n");
            sb.append("Exec=\"" + selectedExecutablePath + "\"\n");
            sb.append("Name=" + strExecutableName + "\n");
            sb.append("Icon=" + strExecutableName + strIconName.substring(strIconName.lastIndexOf(".")) + "\n");
            sb.append("Categories=" + getCategories() + "\n");
            sb.append("Path=" + strExecutableParentPath + "\n");

            fw.write(sb.toString());
            fw.close();
        }

        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

     protected boolean copyIcon()
    {
        if(selectedIconPath == null || selectedExecutablePath == null)
        {
            System.out.println("Null inputs...process exiting!");
            return false;
        }

        File iconTo = new File(iconDir + strExecutableName + strIconName.substring(strIconName.lastIndexOf(".")));

        if(iconTo.exists())
        {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("File exists.");
            alert.setHeaderText("Destination File Exists!");
            alert.setContentText(iconTo.toString() + "\nalready exists. Overwrite it?");

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.CANCEL)
                return false;
        }

        FileChannel source = null;
        FileChannel dest = null;

        try
        {
           source = new FileInputStream(selectedIconPath).getChannel();
           dest = new FileOutputStream(iconTo).getChannel();

           if(source != null && dest != null)
           {
               System.out.println("Copying: " + selectedIconPath.toString() + " to "  + iconTo.toString());
               dest.transferFrom(source, 0, source.size());
               source.close();
               dest.close();
           }
        }
        catch(FileNotFoundException fnf)
        {
            fnf.printStackTrace();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Copy icon to: " + iconTo.toString());
        return true;
    }

    protected StringBuilder getCategories()
    {
        ObservableList<String> ol  = listView.getSelectionModel().getSelectedItems();
        StringBuilder sb = new StringBuilder();
        ol.forEach((a)->{
            sb.append(a + ";");
        });
            return sb;
     }
}

