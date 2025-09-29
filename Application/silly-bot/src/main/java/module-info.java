module silly.bot {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens silly.bot to javafx.fxml;
    exports silly.bot;
}
