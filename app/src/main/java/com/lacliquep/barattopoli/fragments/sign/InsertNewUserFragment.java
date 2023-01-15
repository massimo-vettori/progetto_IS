package com.lacliquep.barattopoli.fragments.sign;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.lacliquep.barattopoli.InsertNewItemActivity;
import com.lacliquep.barattopoli.MainActivity;
import com.lacliquep.barattopoli.MyCameraActivity;
import com.lacliquep.barattopoli.R;
import com.lacliquep.barattopoli.SignActivity;
import com.lacliquep.barattopoli.classes.BarattopoliUtil;
import com.lacliquep.barattopoli.classes.Location;
import com.lacliquep.barattopoli.classes.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * this activity performs the insertion of a new user in the database
 * @author pares, jack, gradiente
 * @since 1.0
 */
public class InsertNewUserFragment extends Fragment {

    //tag name for the logcat
    final private static String ACTIVITY_TAG_NAME = "InsertNewUserFragment";

    //get an instance of the FirebaseAuth
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final String basicImage = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";

    private View view;
    private ImageView imageContainer;
    private Button takePictureUser, register, privacy;
    private EditText insertUsername, insertName, insertSurname, insertCountry, insertRegion, insertProvince, insertCity;
    private EditText insertEmail, insertPassword, confirmPassword;
    private CheckBox checkBox, checkBoxAge;
    private ProgressBar progressBar;

    private String txtEmail = "", txtPassword = "", txtConfirmPassword = "", txtUsername = "", txtName = "", txtSurname = "", txtCountry = "", txtRegion = "", txtProvince = "", txtCity = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_insert_new_user, container, false);

        Bundle b = requireActivity().getIntent().getExtras();
        //array to enable its content be used from an inner class
        String[] encodedImage = new String[1];
        String userBasicInfo = "";
        //initialize the image with the basic one
        encodedImage[0] = basicImage;
        //retrieving an image if this Activity is started by camera Activity
        if (b != null) {
            String res = b.getString(getString(R.string.Bundle_tag_encoded_image));
            encodedImage[0] = res != null? res : basicImage;
            /*String res2 = b.getString(getString(R.string.Bundle_tag_user_basic_info));
            userBasicInfo = res2 != null? res2 : "";*/
        }
        //first things first: force the user to take a picture before any other choice
        //reason: otherwise, when coming back to this activity, all the preferences will be deleted
        if(encodedImage[0].equals(basicImage)) takePicture();
        //set the image

        //image
        imageContainer = view.findViewById(R.id.image_container);
        //setting the image
        imageContainer.setImageBitmap(BarattopoliUtil.decodeFileFromBase64(encodedImage[0]));
        //Buttons
        takePictureUser = view.findViewById(R.id.take_picture_user);
        register = view.findViewById(R.id.register);
        //EditText
        insertUsername = view.findViewById(R.id.insert_username);
        insertName = view.findViewById(R.id.insert_name);
        insertSurname = view.findViewById(R.id.insert_surname);
        insertCountry = view.findViewById(R.id.insert_country);
        insertRegion = view.findViewById(R.id.insert_region);
        insertProvince = view.findViewById(R.id.insert_province);
        insertCity = view.findViewById(R.id.insert_city);
        insertEmail = view.findViewById(R.id.insert_email);
        insertPassword = view.findViewById(R.id.insert_password);
        confirmPassword = view.findViewById(R.id.confirm_password);
        privacy = view.findViewById(R.id.privacy);

        //checkBox
        // TODO add privacy form
        checkBox = view.findViewById(R.id.check_box);
        checkBoxAge = view.findViewById(R.id.check_box_age);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.INVISIBLE);


        register.setOnClickListener(v -> {
            //fetch text inserted in the fields
            txtEmail = insertEmail.getText().toString();
            txtPassword = insertPassword.getText().toString();
            txtConfirmPassword = confirmPassword.getText().toString();
            txtUsername = insertUsername.getText().toString();
            txtName = insertName.getText().toString();
            txtSurname = insertSurname.getText().toString();
            txtCountry = insertCountry.getText().toString();
            txtRegion = insertRegion.getText().toString();
            txtProvince = insertProvince.getText().toString();
            txtCity = insertCity.getText().toString();

            boolean checkedPrivacy = checkBox.isChecked();
            boolean checkedAge = checkBoxAge.isChecked();

            reg(checkedPrivacy, checkedAge, encodedImage[0]);
        });

        takePictureUser.setOnClickListener(v -> {
            takeNewPicture();
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder b = new android.app.AlertDialog.Builder(getContext());
                b.setMessage("Privacy\n" +
                        "Informativa ai sensi del Regolamento UE 2016/679 (“GDPR”)\n" +
                        "Quali tipi di dati raccogliamo?\n" +
                        "Quando usi i nostri servizi, accetti che la nostra azienda raccolga alcuni tuoi dati personali. Questa pagina ha lo scopo di dirti quali dati raccogliamo, perché e come li usiamo.\n" +
                        "Trattiamo due tipi di dati:\n" +
                        "dati forniti dall’utente\n" +
                        "dati che raccogliamo automaticamente\n" +
                        "Dati forniti dall'utente\n" +
                        "Quando ti registri inserisci o rispondi ad un annuncio ti chiediamo di fornirci alcuni dati che servono per poter usufruire del nostro servizio.\n" +
                        "Questi sono, ad esempio, i dati che ti chiediamo:\n" +
                        "indirizzo, email e password  e numero di telefono cellulare\n" +
                        "altri dati relativi all’oggetto del tuo annuncio (ad esempio se inserisci un annuncio per il baratto del tuo orologio ti chiediamo la marca, il modello ed altre informazioni rilevanti)  \n" +
                        "Puoi anche scegliere di fornirci le seguenti informazioni:\n" +
                        "nome\n" +
                        "data di nascita\n" +
                        "sesso\n" +
                        "città o comune di riferimento\n" +
                        "documento d’identità, ai fini di identificazione per talune categorie di annunci\n" +
                        "Il tuo numero di telefono è necessario al completamento di una specifica procedura di identificazione necessaria per accedere al Servizio.\n" +
                        "Potrai modificare il tuo numero di telefono nelle impostazioni della tua area riservata; tale numero verrà nascosto e non apparirà negli annunci pubblicati in talune categorie, salvo che tu decida di renderlo visibile.\n" +
                        "Inoltre il numero di telefono potrebbe essere richiesto nel form di contatto dell’annuncio di un altro utente.\n" +
                        "Per facilitare la registrazione e l’accesso al servizio, ti potrai inoltre autenticare tramite la login del tuo Account Google, dando la possibilità a Google di venire a conoscenza della tua visita al nostro sito. In tal caso autorizzerai Google a condividerci dati quali nome, cognome ed indirizzo email.\n" +
                        "Nell’ambito dell’utilizzo del servizio puoi usufruire delle funzioni “Messaggi” per comunicare rispettivamente con gli altri utenti. Per maggiori approfondimenti ti invitiamo a leggere la sezione “Messaggi e Chat”.\n" +
                        "Dati di terzi\n" +
                        "Se fornisci dati personali di terzi, come ad esempio quelli dei tuoi familiari o amici, devi essere sicuro che questi soggetti siano stati adeguatamente informati e abbiano acconsentito al relativo trattamento nelle modalità descritte dalla presente informativa.\n" +
                        "Dati di minori di anni 16\n" +
                        "Se hai meno di 16 anni non puoi fornirci alcun dato personale né puoi registrarti su Barattopoly, ed in ogni caso non assumiamo responsabilità per eventuali dichiarazioni mendaci da te fornite. Qualora ci accorgessimo dell’esistenza di dichiarazioni non veritiere procederemo con la cancellazione immediata di ogni dato personale acquisito.\n" +
                        "Dati che raccogliamo automaticamente\n" +
                        "Raccogliamo i seguenti dati mediante i servizi che utilizzi:\n" +
                        "dati tecnici: ad esempio indirizzo IP,  dati relativi alla posizione attuale (approssimativa) dello strumento che stai utilizzando, informazioni sul tuo device;\n" +
                        "dati raccolti utilizzando i cookie o tecnologie similari: per ulteriori informazioni, ti invitiamo a visitare la sezione “Cookie”.\n" +
                        " \n" +
                        " \n" +
                        "1. Come utilizziamo i dati raccolti?\n" +
                        "Utilizziamo i dati raccolti per offrirti ogni giorno il nostro servizio e per proporti un servizio più personalizzato ed in linea con i tuoi interessi.\n" +
                        "1.1. Per garantirti l’accesso ai nostri servizi e migliorarne l’erogazione\n" +
                        "Utilizziamo i tuoi dati per garantirti l’accesso ai nostri servizi e la loro erogazione, tra cui:\n" +
                        "registrazione e creazione dell’area riservata.\n" +
                        "pubblicazione degli annunci\n" +
                        "utilizzo delle funzionalità “preferiti” e “ricerche salvate”\n" +
                        "comunicazioni connesse all’erogazione del servizio\n" +
                        "messaggistica tra utenti (Messaggi)\n" +
                        "Tali trattamenti sono necessari per erogare correttamente i servizi di Subito nei confronti degli utenti che vi aderiscono.\n" +
                        "Utilizziamo i tuoi dati anche per assicurare un servizio migliore ed implementarlo, attraverso i seguenti trattamenti:\n" +
                        "analisi dei dati in forma aggregata\n" +
                        "revisione degli annunci, moderazione o rimozione dei contenuti, prevenzione delle frodi\n" +
                        "rilevamento della tua Posizione attuale (approssimativa) per facilitare la fruizione di alcune funzioni del servizio, come ad esempio la visualizzazione degli annunci di utenti a te vicini\n" +
                        "comunicazioni inerenti a servizi analoghi a quelli da te utilizzati\n" +
                        "ricerche di mercato, Sondaggi facoltativi e attività di rilevazione del grado di soddisfazione dell’utenza\n" +
                        "Tali trattamenti si basano sul legittimo interesse  del Titolare  al miglioramento del servizio ed alla sua implementazione e puoi opporti, nei casi previsti dalla legge, in ogni momento.\n" +
                        "1.3. Per offrirti un servizio personalizzato\n" +
                        "Elaboriamo i dati raccolti, qualora tu ci abbia fornito espressamente il consenso, per analizzare le tue abitudini o scelte di consumo al fine di proporti un servizio sempre più personalizzato ed in linea con i tuoi interessi.\n" +
                        "2. Il conferimento dei dati è obbligatorio?\n" +
                        "Il conferimento dei dati personali è obbligatorio esclusivamente per i trattamenti necessari all’erogazione dei servizi offerti da Barattopoly (l’eventuale rifiuto per finalità di erogazione del servizio rende impossibile l’utilizzo del servizio stesso).\n" +
                        " \n" +
                        "3. Soggetti a cui possono essere comunicati dati personali\n" +
                        "I  dati raccolti nell’ambito dell’erogazione del servizio potranno essere comunicati a:\n" +
                        "società che svolgono funzioni strettamente connesse e strumentali all’operatività – anche tecnica – dei servizi di Barattopoly\n" +
                        "enti ed autorità amministrative e giudiziarie in virtù degli obblighi di legge\n" +
                        "I tuoi dati personali potrebbero essere trasferiti al di fuori dell’Unione Europea per essere trattati da alcuni dei nostri fornitori di servizi. In questo caso, ci assicuriamo che questo trasferimento avvenga nel rispetto della legislazione vigente e che sia garantito un livello adeguato di protezione dei dati personali basandoci su una decisione di adeguatezza, su clausole standard definite dalla Commissione Europea o su Binding Corporate Rules.\n" +
                        "4. Come puoi avere informazioni sui dati, modificarli, cancellarli o averne una copia?\n" +
                        "4.1. Accesso ai dati personali dalla tua area riservata e revoca del consenso (opt-out)\n" +
                        "Puoi, in qualsiasi momento, visionare i tuoi dati e le informazioni eventualmente fornite per la fatturazione nella tua area riservata. Entra nel tuo account e clicca la sezione “Area Riservata”.\n" +
                        "Ricordati che puoi revocare in qualsiasi momento i consensi che hai fornito accedendo alla tua area riservata e rimuovendo il relativo flag.\n" +
                        "Per esercitare l’opt out dalle notifiche push sul tuo device Android.\n" +
                        "4.3. Esercizio dei tuoi diritti\n" +
                        "Qualsiasi persona fisica che utilizzi il nostro servizio può:\n" +
                        "ottenere dal titolare, in ogni momento, informazioni circa l’esistenza dei propri dati personali, l’origine degli stessi, le finalità e le modalità di trattamento e, qualora presenti, di ottenere l’accesso ai dati personali ed alle informazioni di cui all’articolo 15 del GDPR\n" +
                        "richiedere l’aggiornamento, la rettifica, l’integrazione, la cancellazione, la limitazione del trattamento dei dati nel caso ricorra una delle condizioni previste all’articolo 18 del GDPR, la trasformazione in forma anonima o il blocco dei dati personali, trattati in violazione di legge, compresi quelli di cui non è necessaria la conservazione in relazione agli scopi per i quali i dati sono stati raccolti e/o successivamente trattati\n" +
                        "opporsi, in tutto o in parte, per motivi legittimi e secondo quanto previsto dalla legge, al trattamento dei dati, ancorché pertinenti allo scopo della raccolta ed al trattamento dei dati personali previsti ai fini di informazione commerciale o di invio di materiale pubblicitario o di vendita diretta ovvero per il compimento di ricerche di mercato o di comunicazione commerciale. Ogni utente ha altresì il diritto di revocare il consenso in qualsiasi momento senza pregiudicare le liceità del trattamento basata sul consenso prestato prima della revoca\n" +
                        "ricevere i propri dati personali, trattati sulla base di un contratto o sul consenso dell’interessato, forniti consapevolmente ed attivamente o attraverso la fruizione del servizio, in un formato strutturato, di uso comune e leggibile da dispositivo automatico, e di trasmetterli ad un altro titolare del trattamento senza impedimenti\n" +
                        "proporre reclamo presso l’Autorità Garante per la protezione dei dati personali in Italia\n" +
                        "5. Come e per quanto tempo i tuoi dati saranno conservati?\n" +
                        "La conservazione dei dati personali avverrà in forma cartacea e/o elettronica/informatica e per il tempo strettamente necessario al soddisfacimento delle finalità di cui al punto 1, nel rispetto della tua privacy e delle normative vigenti.\n" +
                        "Se non eserciti alcuna azione attiva (ad esempio navigazione, ricerche e/o ogni altra modalità di utilizzo del servizio) su Barattopoly per un periodo di 27 mesi, verrai classificato come utente inattivo e i tuoi dati personali saranno cancellati automaticamente dalla piattaforma.\n" +
                        "Per finalità di marketing diretto e profilazione trattiamo i tuoi dati, secondo il consenso che hai fornito, per un periodo massimo pari a quello previsto dalla normativa applicabile (rispettivamente pari a 24 e 12 mesi).\n" +
                        "Per finalità di sviluppo e miglioramento del servizio e relative funzionalità, i dati personali dell’utente attivo potranno essere analizzati e conservati in forma aggregata e pseudoanonimizzata fino a 36 mesi. Tali dati sono riservati e ad esclusivo uso interno.\n" +
                        "Per le medesime finalità ed una maggiore sicurezza del servizio mediante attività di moderazione e prevenzione di condotte contrarie alle condizioni e alle regole editoriali di Barattopoly – anche in collaborazione con le Autorità – gli annunci, i contenuti generati dall’utente sulla piattaforma e quelli relativi alla sua identificazione saranno in ogni caso conservati dal titolare, in forma protetta e con accesso limitato, per un periodo pari a 36 mesi. Tali contenuti riservati saranno ad esclusivo uso interno e potranno essere forniti dal titolare solo all’Autorità Giudiziaria o al difensore dell’utente per finalità di giustizia.\n" +
                        "Nel caso di esercizio del diritto all’oblio attraverso richiesta di cancellazione espressa dei dati personali trattati dal titolare, ti ricordiamo che tali dati saranno conservati, in forma protetta e con accesso limitato, unicamente per finalità di accertamento e repressione dei reati, per un periodo non superiore ai 12 mesi dalla data della richiesta (salvo quanto sopra indicato in merito ai dati utilizzati ai fini di moderazione e prevenzione) e successivamente saranno cancellati in maniera sicura o anonimizzati in maniera irreversibile.\n" +
                        "Ti rammentiamo infine che per le medesime finalità, i dati relativi al traffico telematico degli utenti attivi, esclusi comunque i contenuti delle comunicazioni, saranno conservati per un periodo non superiore ai 6 anni dalla data di comunicazione.\n" +
                        "6. Come assicuriamo la protezione dei tuoi dati?\n" +
                        "I dati sono raccolti dai soggetti indicati al punto 3, secondo le indicazioni della normativa di riferimento, con particolare riguardo alle misure di sicurezza previste dal GDPR (art. 32) per il loro trattamento mediante strumenti informatici, manuali ed automatizzati e con logiche strettamente correlate alle finalità indicate al punto 1 e comunque in modo da garantire la sicurezza e la riservatezza dei dati stessi.\n" +
                        "Nel rispetto della normativa applicabile, è attivo un sistema di verifica antispam sulle comunicazioni tra utenti. I dati ivi inseriti potranno essere verificati al solo scopo di individuare attività illecite o contenuti non conformi alle Condizioni generali del Servizio, ma non saranno trattati o comunicati per finalità commerciali o promozionali.\n" +
                        "7. Ulteriori informazioni\n" +
                        "7.1. Messaggi e Chat\n" +
                        "La funzione Messaggi è rivolta ad una comunicazione immediata fra:\n" +
                        "utenti dei servizi di Barattopoly che interagiscono tra loro esclusivamente per lo scambio dei propri beni o servizi; nell’ambito di tale servizio gli utenti online sono contrassegnati dalla relativa indicazione;\n" +
                        "Potremmo verificare il contenuto dei Messaggi al fine di moderare gli stessi per finalità di sicurezza e per preservare la netiquette e le regole editoriali di Barattopoly.\n" +
                        "L’utilizzo del servizio Messaggi comporta la possibilità che l’identità dell’utente (così come indicata all’atto della registrazione/erogazione dei servizi di Barattopoly) e relativi contenuti siano resi noti durante le sessioni di attività.\n" +
                        "Ci riserviamo il diritto di escludere dal servizio Messaggi, e più in generale dai servizi offerti da Barattopoly, tutti coloro che non osservino le regole e/o non rispettino le finalità definite per l’utilizzo del servizio Messaggi, nonché tutti coloro che adottino comportamenti non corretti e/o non rispettosi delle persone.\n" +
                        " \n" +
                        "8. L’informativa sulla privacy può subire modifiche nel tempo?\n" +
                        "La presente informativa potrebbe essere soggetta a modifiche. Qualora vengano apportate sostanziali modifiche all’utilizzo dei dati relativi all’utente da parte del Titolare, quest’ultimo avviserà l’utente pubblicandole con la massima evidenza sulle proprie pagine o tramite mezzi alternativi o similari.\n" +
                        " \n" +
                        " \n" +
                        " \n" +
                        " \n")
                        .setCancelable(false)
                        .setPositiveButton("Letto", ((dialogInterface, i) -> dialogInterface.cancel())).show();
            }
        });

        return view;

    }

    private void reg(boolean checkedPrivacy, boolean checkedAge, String image) {
        String empty = getString(R.string.empty_text);
        if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtEmail, getString(R.string.email) + empty)) {
            if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtPassword, getString(R.string.password) + empty)) {
                if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtConfirmPassword, getString(R.string.confirm_password) + empty)) {
                    if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtCountry, getString(R.string.insert_country) + empty)) {
                        if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtRegion, getString(R.string.insert_region) + empty)) {
                            if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtProvince, getString(R.string.insert_province) + empty)) {
                                if (BarattopoliUtil.checkMandatoryTextIsNotEmpty(getActivity(), txtCity, getString(R.string.insert_city) + empty)) {
                                    if (txtPassword.length() < 6) Toast.makeText(getActivity(), getString(R.string.error_password), Toast.LENGTH_SHORT).show();
                                    else {
                                        if (!(txtPassword.equals(txtConfirmPassword))) Toast.makeText(getActivity(), getString(R.string.password) + ", " + getString(R.string.password_confirm) + getString(R.string.match_error), Toast.LENGTH_SHORT).show();
                                        else {
                                            if (!checkedPrivacy) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.accept_privacy), Toast.LENGTH_LONG).show();
                                            else {
                                                if (!checkedAge) Toast.makeText(getActivity(), getString(R.string.select_error) + getString(R.string.age_confirm), Toast.LENGTH_LONG).show();
                                                else {
                                                    if (Location.checkLocation(getActivity(), txtCountry, txtRegion, txtProvince, txtCity)) {
                                                        ArrayList<String> location = new ArrayList<>(Arrays.asList(txtCountry, txtRegion, txtProvince, txtCity));
                                                        registration(txtEmail, txtPassword, location, image);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Log.d("User", "0");
        }
    }

    /**
     * check the SDK version in order to handle the registration in background
     * @param email the provided email from the user
     * @param password the provided password from the user
     */
    private void registration(String email, String password, ArrayList<String> location, String image) {
        // TODO find out which is the eldest SDK version accepting concurrent
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            // Do something for R and above versions
            //using concurrent executors
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                //Background work here
                registerUser(email, password, location, image);
                handler.post(() -> {
                    //UI Thread work here
                    progressBar.setVisibility(View.VISIBLE);
                });
            });

        } else {
            // do something for phones running an SDK before R
            String[] values = new String[7];
            values[0] = email;
            values[1] = password;
            int i = 2;
            for(String s: location) {
                values[i] = s;
                i++;
            }
            values[6] = image;
            new AsyncRegister().execute(values);
        }
    }

    /**
     * class to handle registration in asynchronous way before SDK R
     */

    @SuppressLint("StaticFieldLeak")
    private class AsyncRegister extends AsyncTask<String, Integer, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            ArrayList<String> location = new ArrayList<>(Arrays.asList(strings[2], strings[3], strings[4], strings[5]));
            registerUser(strings[0], strings[1], location, strings[6]);
            //TODO delete or improve publishProgress
            for (int i = 0; i < 100; ++i) publishProgress(i);
            return null;
        }
        // TODO: add a progression bar or sth? delete or improve onProgressUpdate
        protected void onProgressUpdate(Integer... integers) {
            //Toast.makeText(getActivity(), getString(R.string.in_progress), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(integers[0]);
        }

    }
    /**
     * registration of the user in the database with {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    private void registerUser(String email, String password, ArrayList<String> location, String image) {
        //addOnCompleteListener is added to display a Toast for confirmation of the registration
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                //positive feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.success), Toast.LENGTH_SHORT).show();
                //login of user
                loginUser(email, password);
                String userId = auth.getUid();
                if (userId != null) User.insertUserInDataBase(userId,txtUsername,txtName,txtSurname,location,image);
            } else {
                String error = (Objects.requireNonNull(task.getException()).getMessage());
                //negative feedback
                Toast.makeText(getActivity(), getString(R.string.Registration) + getString(R.string.failure) + ": \n" + error, Toast.LENGTH_SHORT).show();
            }

        });
    }
    /**
     * Login of the user in the database using {@link FirebaseAuth}
     * @param email the provided email
     * @param password the provided password
     */
    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
            Toast.makeText(getActivity(), getString(R.string.Login) + getString(R.string.success), Toast.LENGTH_SHORT).show();
            //TODO: after login start MainActivity
            startActivity(new Intent(getActivity(), MainActivity.class));
        });
    }

    /**
     * take a picture for the new item after warning the user about the non persistence of their chosen options
     */
    private void takePicture() {
        //take a new picture
        Fragment f = new InsertNewUserFragment();
        Intent intent = new Intent(requireActivity(),MyCameraActivity.class);
        Bundle c = new Bundle();
        //give to the next activity the fully qualified name of this class in order to enable it to return here
        c.putString(getString(R.string.Bundle_tag_Previous_activity), "InsertNewUserFragment");
        intent.putExtras(c);
        //go to camera activity
        startActivity(intent);
    }
    private void takeNewPicture() {
        //pop up alert to warn about taking a new picture
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage(getString(R.string.Alert_take_new_picture))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        takePicture();
                    }
                })
                .setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //return to setting the item details and don't take a new picture
                        dialog.cancel();
                    }
                }).show();

    }
}
