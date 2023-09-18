package com.example.donwloadwebcontent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity { //всегда, когда читаем данные из интернета, у приложения должен быть разрешен доступ в интернет
    //для того что бы получить разрешение нам нужно их добавить в AndroidManifest.xml

    private String mailRu = "https://mail.ru/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //запускаем метод
        DonwloadTask task = new DonwloadTask();
        try {
            String result = task.execute(mailRu).get(); //метод выводящий наши данные
            Log.i("URL", result); //и выводим в нашем логе
        } catch (ExecutionException e) {
            e.printStackTrace(); //throw new RuntimeException(e);
        } catch (InterruptedException e) {
            e.printStackTrace(); //throw new RuntimeException(e);
        }

    }

    //создаем класс который нужен для того, что бы наша задача выполнялась в другом потоке, отличного от главного потока
    //(т.к. согласно политике And, любой код, который будет выполняться какое-то длительное время должен быть запущен в другом потоке (UI Thread - поток пользовательского интерфейса)
    private static class DonwloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            //Загрузка контента
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]); //создали URL из строки , которую передали

                //открываем соединение
                urlConnection = (HttpURLConnection) url.openConnection(); //открыли соденинение (подобно как в браузере открываем сайт)

                //для того что бы читать данные из интернета, создаем поток ввода
                InputStream in = urlConnection.getErrorStream();
                //начало процесса чтения
                InputStreamReader reader = new InputStreamReader(in);// что  бы прочитать эти данные мы создали  InputStreamReader reader из нашего потока ввода
                // InputStreamReader - может читать данные только по одному символу
                //что бы читать данные не по одному символу, а сразу строками , создаем
                BufferedReader bufferedReader = new BufferedReader(reader); //создали его из InputStreamReader
                //создаем строку
                String line = bufferedReader.readLine(); //Начинаем процесс чтения данных - читаем одну строку из ридера и заносим ее в переменную line
                // теперь мы получили: первую строчку из сайта, теперь надо сохранить строку в StringBuilder result , а после прочитать следующую строку
                //и так делаем пока метод .readLine()  не вернет null
                while (line != null) { //до тех пор пока все данные не будут прочитаны
                    result.append(line); // прочитав строку добавляем ее в StringBuilder result
                    line = bufferedReader.readLine(); // и переменной line присваиваем значение след строки
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();//throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();//throw new RuntimeException(e);
            } finally { //блок нужен если, допустим, если при чтении данных произошли какие-то проблемы
                // в этом случае сработает исключение и выполнится блок catch  и наше соединение останется открытым, независимо успешно или нет была вся ситуация, нам нужно закрыть соединение
                //для этого проверяем
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

            }
            return result.toString();
        }
    }
}