FindYourBook
Developed for the Software Engineering and Web Design course, University of Rome Tor Vergata.


## Description
FindYourBook is a Java-based educational and book-management platform that connects readers, allows them to search for books by various criteria, manage their personal library statuses (To Read, Reading, Read), and track reading activities.
Readers can search for books, manage their personal library and reading statuses, and track reading progress.
Publishing House (Casa Editrice) can manage the catalogue, publish new books, and view statistics on how many times the books in their catalogue have been read by application users.

## Tecnologies
* Java 17
* Maven
* MySQL
* JavaFX
* SendGrid API (notifiche email)

## Architecture
BCE (Boundary-Control-Entity)- **MVC** (Model-View-Controller) pattern with clear separation between:
* `controller/applicativo` — business logic
* `controller/cli` — CLI user interface
* `controller/gui` — GUI user interface
* `view/cli` — CLI boundary view
* `view/gui` — GUI boundary view
* `dao` — data access layer (DB, File, Memory)
* `model` — domain entities
* `bean` — data transfer objects
* `pattern` — GoF patterns (Singleton, Observer, State)

The system supports three persistence mode:
* **DATABASE** — MySQL (full-version)
* **FILE/JSON** — full-version
* **MEMORY** — in-memory / demo-version (used in unit tests as well)


## Getting started
At startup, the application asks to select the persistence mode:

** Demo → simulated in-memory data
** Database → MySQL persistence
** File → JSON file persistence

** Then, the interface must be selected:

** CLI → text-based interface
** GUI → graphical interface
**To use the database mode, create and configure the following file: 

**`src/main/resources/db.properties` 
** with the following structure:

** CONNECTION_URL=jdbc:mysql://localhost:3306/findyourbookdb
** LOGIN_USER=root
** LOGIN_PASS=
** SENDGRID_API_KEY=TUA_API_KEY
** SENDGRID_FROM_EMAIL=findyourbook.support@gmail.com

** ⚠️ The db.properties file includes database credentials and a placeholder for the SendGrid API key. Email notifications in action can be seen in the project demo video.

** Demo credentials
** Role	        Username	              Password
** Reader	       mario	                qualsiasi
** Publisher	   mondadori              	qualsiasi

** Database credentials (modalità MySQL)
** Role	        Username	                Password
** Reader	       mario	                Password123!
** Publisher	    mondadori              	Password123!

**Other test accounts available, see BrainBank_db.sql for the full list.

## Author
Cicchetta Aurora

  
