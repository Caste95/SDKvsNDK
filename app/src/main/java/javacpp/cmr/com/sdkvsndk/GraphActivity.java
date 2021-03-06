package javacpp.cmr.com.sdkvsndk;

/*
  Activity che mi crea il grafico dei tempi degli algoritmi
  Usiamo come libreria per il grafico: Graph View
  Sito con tutta la docomentazione: http://www.android-graphview.org/
  Obbiettivo di questa activity è di plottare i tempi di java e c dei vari algoritmi
  L'asse delle y sarà il tempo in millisecondi
  L'asse delle x sarà gli input che sono stati dati agli algoritmi
  Per quanto riguarda gli stessi input sarà fatta la media
  Inoltre se l'utente vuole vedere tutti i dati per gli stessi input il grafico è "toccabile"
  e verranno stampati tutti i dati su delle TextBox
  Il grafico è stato reso scrollabile e zoomabile per essere più leggibile
  I dati che usa il grafico sono delle Series e come punti dei DataPoint
  Una Series quindi e un insieme di DataPoint
  Questi dati sono presi dal db e le Series sono direttamente generate nella classe AlgorithmView
  Una per i tempi del c e una per i tempi del java
 */

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class GraphActivity extends AppCompatActivity {

    private TextView datiC;
    private TextView datiJava;
    private TextView datiInput;
    private TextView in;
    private TextView c;
    private TextView j;

    //variabili di utilizzo
    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        //prendo gli id dell'interfaccia
        TextView titolo = (TextView) findViewById(R.id.titolo);
        in = (TextView) findViewById(R.id.In);
        c = (TextView) findViewById(R.id.c);
        j = (TextView) findViewById(R.id.j);
        datiInput = (TextView) findViewById(R.id.datiInput);
        datiC = (TextView) findViewById(R.id.datiC);
        datiJava = (TextView) findViewById(R.id.datiJava);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        //setto le due textview scrollabili in caso abbia più dati di quelli che ci possano stare
        datiC.setMovementMethod(new ScrollingMovementMethod());
        datiJava.setMovementMethod(new ScrollingMovementMethod());

        //recupero i dati passati dall'intent e setto il titolo
        pos = getIntent().getIntExtra("pos", 0);
        titolo.setText(AlgorithmView.LIST[pos].getNome());

        //qui faccio la serie che stampera il grafico(dovo prendera dal db)
        //usero la funzione implementata in AlgoritmhView
        LineGraphSeries<DataPoint> seriesC = AlgorithmView.LIST[pos].getSeriesC(this);
        LineGraphSeries<DataPoint> seriesJava = AlgorithmView.LIST[pos].getSeriesJava(this);

        //setto alcuni parametri delle serie del c
        //setto il colore per differenziare le due serie
        seriesC.setColor(Color.RED);
        //non sono stati dati i titoli ai grafici con le funzioni della libreria perchè purtroppo
        //spostavano il grafico e non appariva tutto sullo schermo
        seriesC.setDrawDataPoints(true); //evidenziare i punti di interesse(datapoint)
        seriesC.setDataPointsRadius(8); //dimensione del datapoint
        //li facciamo anche cliccabili cosi da stamparci un toast per vedere bene i dati

        //setto alcuni parametri delle serie del java
        //setto il colore per differenziare le due serie
        seriesJava.setColor(Color.BLUE);
        //non sono stati dati i titoli ai grafici con le funzioni della libreria perchè purtroppo
        //spostavano il grafico e non appariva tutto sullo schermo
        seriesJava.setDrawDataPoints(true); //evidenziare i punti di interesse(datapoint)
        seriesJava.setDataPointsRadius(8); //dimensione del datapoint

        //inserisco la serie dei tempi del c
        graph.addSeries(seriesC);

        //inserire la serie dei tempi del java
        graph.addSeries(seriesJava);

        //ora mettiamo la leggenda
        seriesC.setTitle("C");
        seriesJava.setTitle("Java");
        graph.getLegendRenderer().setVisible(true);

        //ora abilitiamo lo zoom e lo scrool del grafico
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);

        //faccio cliccabili le due serie cosi da poter visualizzare i dati con cui ha fatto la media
        //e stamparli sulla text box sotto
        /*
            * PROBLEMA CON I LISTNER:
            * i listener funzionano ma con i dati sovrapposti anche se clicco una sola volta me lo
            * fa due volte perchè è come se avessi toccato entrambi i punti quindi volendo basta un
            * solo listner perchè tanto mi basta solo un dato che è quello dell'input
         */
        seriesC.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int input = (int) dataPoint.getX(); //prendo l'input
                String outc = "", outjava = ""; //stringhe per l'output
                int x[][] = AlgorithmView.LIST[pos].getDataByInput(GraphActivity.this, input);
                //x non dovrebbe essere mai null ma non si sa mai quindi faccio questo if
                //in caso sia null non faccio niente
                if(x != null){
                    for (int[] aX : x) {
                        outc = outc + aX[0] + "\n";
                        outjava = outjava + aX[1] + "\n";
                    }
                }
                in.setText(R.string.Input);
                c.setText(R.string.c);
                j.setText(R.string.java);
                datiInput.setText(Integer.toString(input));
                datiC.setText(outc);
                datiJava.setText(outjava);
            }
        });

        /*
            * implemento lo stesso anche l'altro listner in caso i grafici siano distanti cosi
            * l'utente non si "preoccupera" in caso non gli appaia qualcosa del grafico anche se ha toccato
            * tanto per quanto riguarda l'efficenza ho visto che il programma non ne risente
         */
        seriesJava.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                int input = (int) dataPoint.getX(); //prendo l'input
                String outc = "", outjava = ""; //stringhe per l'output
                int x[][] = AlgorithmView.LIST[pos].getDataByInput(GraphActivity.this, input);
                //x non dovrebbe essere mai null ma non si sa mai quindi faccio questo if
                //in caso sia null non faccio niente
                if(x != null){
                    for (int[] aX : x) {
                        outc = outc + aX[0] + "\n";
                        outjava = outjava + aX[1] + "\n";
                    }
                }
                in.setText(R.string.Input);
                c.setText(R.string.c);
                j.setText(R.string.java);
                datiInput.setText(Integer.toString(input));
                datiC.setText(outc);
                datiJava.setText(outjava);
            }
        });

    }

}