package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.*;

public class HelpWindowController implements Initializable {

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu menu;
    @FXML
    private MenuItem item1;
    @FXML
    private MenuItem item2;
    @FXML
    private MenuItem item3;
    @FXML
    private MenuItem item4;
    @FXML
    private TextArea info;

    private Profile profile;

    public void setUsuario(Profile profile) {
        this.profile = profile;
    }

    public void loadAdminItems() {
        item1.setText("Shop Management");
        item2.setText("Add Game");
        item3.setText("Modify Game");
        item4.setVisible(true);
        item4.setDisable(false);
        item4.setText("Delete Game");
    }

    @FXML
    private void changeHelpText1() {
        if (profile instanceof Admin) {
            info.setText("Bienvenido a Store Management. Esta página permite gestionar el catálogo de videojuegos.\n"
                    + "\n"
                    + "Operaciones disponibles:\n"
                    + "\n"
                    + "Selecciona un videojuego: Haz clic en cualquier fila de la tabla para seleccionar el juego\n"
                    + "Verifica los detalles: El juego seleccionado aparecerá en la sección 'Selected game'\n"
                    + "Añadir juego: Usa el botón 'Add Game' para abrirse una nueva ventana y añadir un juego nuevo al catálogo\n"
                    + "Modificar juego: Usa el botón 'Modify Game' (o en el menu de click derecho) para editar los detalles del juego seleccionado\n"
                    + "Eliminar juego: Usa el botón 'Delete Game' (o en el menu de click derecho) para eliminar el juego seleccionado del catálogo\n"
                    + "\n"
                    + "Filtros disponibles:\n"
                    + "Search game: Busca por nombre del videojuego\n"
                    + "Genre: Filtra por género (Acción, RPG, etc.)\n"
                    + "Platform: Filtra por plataforma (PC, PlayStation, Xbox, etc.)\n"
                    + "\n"
                    + "Importante:\n"
                    + "Debes seleccionar un videojuego antes de poder modificarlo o eliminarlo. La eliminación es permanente.");
        } else {
            info.setText("Bienvenido a Game Store. Esta página te permite explorar y comprar videojuegos disponibles.\n"
                    + "\n"
                    + " Pasos para comprar:\n"
                    + "\n"
                    + "Selecciona un videojuego: Haz clic en cualquier fila de la tabla para seleccionar el juego\n"
                    + "Verifica los detalles: El juego seleccionado aparecerá en la sección 'Selected game'\n"
                    + "Añade a carrito: Usa el botón 'Add to Cart' para agregar el juego a tu carrito de compras\n"
                    + "Escribe una reseña: Usa el botón 'Review' para acceder a la sección de reseñas\n"
                    + "\n"
                    + "Filtros disponibles:\n"
                    + "\n"
                    + "Search game: Busca por nombre del videojuego\n"
                    + "Genre: Filtra por género (Acción, RPG, etc.)\n"
                    + "Platform:</strong> Filtra por plataforma (PC, PlayStation, Xbox, etc.)\n"
                    + "\n"
                    + "Tu saldo:\n"
                    + "Consulta tu balance actual en la esquina superior izquierda. Puedes añadir saldo usando el botón 'Añadir saldo'.");
        }
    }

    @FXML
    private void changeHelpText2() {
        if (profile instanceof Admin) {
            info.setText("En esta parte, tienes que rellenar todos los campos del juego.\n"
                    + "Name: Nombre de juego."
                    + "Platforms: Uno de las plataformas en el que se puede jugar al juego."
                    + "Company: La compañia que ha creado el juego."
                    + "Stock: La cantidad de copias del juego que se va a añadir."
                    + "Genre: Uno de los generos del juego."
                    + "Price: El precio del juego. El uso de decimales es perimitido."
                    + "PEGI: El PEGI del juego. Puede ser ninguno si el juego no lo tiene aun."
                    + "Release Date: El dia en el que un juego haya salido/vaya a salir en."
                    + "Usa el boton 'Add Game' para añadir el juego.");
        } else {
            info.setText("");
        }
    }

    @FXML
    private void changeHelpText3() {
        if (profile instanceof Admin) {
            info.setText("");
        } else {
            info.setText("");
        }
    }

    @FXML
    private void changeHelpText4() {
        if (profile instanceof Admin) {
            info.setText("");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (profile instanceof Admin) {
            loadAdminItems();
        }
    }
}
