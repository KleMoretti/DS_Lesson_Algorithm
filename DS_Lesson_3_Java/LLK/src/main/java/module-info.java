module com.indi.llk {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires javafx.media;

    opens com.indi.llk to javafx.fxml;
    exports com.indi.llk;
    exports com.indi.llk.Interface_Controller;
    opens com.indi.llk.Interface_Controller to javafx.fxml;
    exports com.indi.llk.Window_Controller;
    opens com.indi.llk.Window_Controller to javafx.fxml;
    exports com.indi.llk.Game_Core_Controller;
    opens com.indi.llk.Game_Core_Controller to javafx.fxml;
}