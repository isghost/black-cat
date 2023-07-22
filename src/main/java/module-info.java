module com.all.tool.alltool {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.antdesignicons;
    requires lombok;
//    requires eu.hansolo.tilesfx;

    opens com.isghost.tool.image to javafx.fxml;
    opens com.isghost.tool.image.utils;
    opens com.isghost.tool.image.consts;
    opens com.isghost.tool.image.model;
    exports com.isghost.tool.image;
}