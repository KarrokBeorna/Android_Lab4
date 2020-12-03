# Цели

- Ознакомиться с принципами работы adapter-based views.
- Получить практические навыки разработки адаптеров для *view*

## Задача 1. Знакомство с библиотекой (unit test)

Начиналось всё грустно: скачал полностью архив курса, нашел эту biblib и запустил в новом проекте. Смотрю, а у нас тут Java. Думаю, хорошо, синтаксис так и так мне понятен, хоть и уродлив, делать всё равно нужно... Начал смотреть все файлы - всё оказалось достаточно просто.
1. Метод strictModeThrowsException():
Копируем код из normalModeDoesNotThrowException, изменяем лишь конец итерирования: теперь он равен не *cfg.maxValid + 1*, а *cfg.maxValid - 1*, так как дальше мы должны попробовать взять нашу нулевую запись, после чего должно обработаться исключение, потому что мы уже не можем её читать.
__Листинг 1.1 - strictMode__

        @Test
          public void strictModeThrowsException() throws IOException {
            BibDatabase database = openDatabase("/references.bib");
            BibConfig cfg = database.getCfg();
            cfg.strict = true;

            BibEntry first = database.getEntry(0);
            for (int i = 0; i < cfg.maxValid - 1; i++) {
              BibEntry unused = database.getEntry(0);
              assertNotNull("Should not throw any exception @" + i, first.getType());
            }

            try {
              BibEntry unused = database.getEntry(0);
              first.getType();
            } catch (IllegalStateException e) {
                System.out.println("ISE: " + e.getMessage());
            }
          }

2. Метод shuffleFlag():
Здесь всё еще проще, мы должны просто попробовать перемешать нашу библиотеку из записей 2 разных видов. Берем нашу библиотеку, перемешиваем и пытаемся получить тип второй записи, как будто он нулевой элемент. Пробуем это выполнить 10 раз. Конечно, есть мизерный шанс, что все 10 раз у нас перемешаются 2 записи таким образом, что вторая запись всегда будет на втором месте, но такое возможно с вероятностью 0.001.
__Листинг 1.2 - shuffleFlag__

        @Test
          public void shuffleFlag() throws IOException {
            boolean ans = false;
            for (int i = 0; i < 10; i++) {
              BibDatabase database = openDatabase("/mixed.bib");
              BibConfig cfg = database.getCfg();
              cfg.shuffle = true;
              if (database.getEntry(0).getType() == Types.MISC) {
                ans = true;
              }
            }
            assertTrue(ans);
          }

Казалось бы, уже 3 курс, однако нас так никогда и не заставляли собирать .jar-файлы. Собственно, это оказалось легко, хотя я бы посоветовал убрать "__./__" из описания работы, чтобы не вводили в заблуждение, ибо в терминале проекта сразу открывается __...\\...\\...\biblib>__ и достаточно прописать "__gradlew build__". (Да-да, у меня удаление 2 символов отняло 30 минут, пока читал статьи в интернете...)
И нашли мы наш созданный джарник в __build/libs/biblib.jar__.

Добавили 1 строчку в settings.gradle(Android_Lab4):

    include ':biblib'
Добавили 1 строчку в build.gradle (:app):

    implementation project(':biblib')
Также сразу пропишем в этом же билде:

    buildFeatures{
        dataBinding = true
        viewBinding = true
    }
Это у нас для следующего задания.

## Задача 2 - Знакомство с RecyclerView.

Здесь же, благодаря видео-лекциям, всё было достаточно просто, Андрей Николаевич нас буквально за ручку вёл. Собственно, так как уже времени мало, бафов никаких не дают, поэтому зачем напрягаться - возьму однородный список.

Создаем основной Layout:
__Листинг 2.1 - activity_main.xml__

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:context=".MainActivity">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/recycler_view"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    </LinearLayout>
Давайте сразу создадим макет для отображения.
__Листинг 2.2 - entries.xml__

    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/background"
            android:padding="5dp"
            android:orientation="vertical">

            <TextView
                android:id="@+id/title"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="@color/title"
                android:textSize="20sp"/>

            <TextView
                android:id="@+id/author"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="@color/author"
                android:textSize="18sp"/>

            <androidx.constraintlayout.widget.ConstraintLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content">

                <TextView
                    android:id="@+id/pages"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textColor="@color/pages"
                    android:textSize="16sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintStart_toStartOf="parent" />

                <TextView
                    android:id="@+id/year"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textColor="@color/year"
                    android:textSize="16sp"
                    app:layout_constraintBottom_toBottomOf="parent"
                    app:layout_constraintEnd_toEndOf="parent" />
            </androidx.constraintlayout.widget.ConstraintLayout>
        </LinearLayout>
        <Space
            android:layout_width="match_parent"
            android:layout_height="5dp"
            android:background="@color/white">
        </Space>
    </LinearLayout>
Выглядит всё это чудо примерно так:

![](https://github.com/KarrokBeorna/Android_Lab4/blob/master/app/1.jpg)

Постарался сделать красивый макет, собственно, кажется красивым :D

Перейдем к части программирования.
Для начала напишем Adapter-класс для наполнения нашего макета:
__Листинг 2.3 - Adapter.kt__

    package com.example.android_lab4

    import android.annotation.SuppressLint
    import android.view.LayoutInflater
    import android.view.ViewGroup
    import androidx.recyclerview.widget.RecyclerView
    import name.ank.lab4.BibDatabase
    import java.io.InputStream
    import java.io.InputStreamReader
    import com.example.android_lab4.databinding.EntriesBinding
    import name.ank.lab4.Keys

    class Adapter(base: InputStream) : RecyclerView.Adapter<Adapter.ViewHolder>() {
        private val database = BibDatabase(InputStreamReader(base))

        class ViewHolder(binding: EntriesBinding) : RecyclerView.ViewHolder(binding.root) {
            val author = binding.author
            val title = binding.title
            val year = binding.year
            val pages = binding.pages
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = EntriesBinding.inflate(inflater, parent, false)
            return ViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val entry = database.getEntry(position % database.size())
            holder.author.text = "Author(s): " + entry.getField(Keys.AUTHOR)
            holder.title.text = "Title: " + entry.getField(Keys.TITLE)
            holder.year.text = "Year: " + entry.getField(Keys.YEAR)
            holder.pages.text = "Pages: " + entry.getField(Keys.PAGES)
        }

        override fun getItemCount(): Int = Int.MAX_VALUE
    }
Давайте поясню, что за что отвечает и вообще как это всё работает:
- на вход класса мы принимаем нашу базу данных, размещенную по пути __res/raw/articles.bib__;
- наш класс наследуется от __RecyclerView.Adapter<Adapter.ViewHolder>()__, причем передаваемый тип __Adapter.ViewHolder__ это наш внутренний класс, в котором мы биндим поля для отображения;
- переопределенный метод *onCreateViewHolder* - обязательный метод для переопределения, который возвращает новый ViewHolder, используемый в методе __onBindViewHolder__. Собственно binding, который мы возвращаем, как раз-таки является тем самым __EntriesBinding__, то есть наш макет .xml для отображения.
- переопределенный метод *onBindViewHolder* - также обязательный метод для переопределения, в котором мы наполняем наши текстовые поля нашими статьями с извлечением полей по ключам. Аннотация __@SuppressLint("SetTextI18n")__ ("I18n" = "Internationalization") служит для скрытия предупреждения о том, что нужно бы вывести текст в ресурсы. Однако мне не нужна многоязыковая поддержка, поэтому просто Alt-Enter - Enter спасает.
- последний метод *getItemCount* - опять обязательный для переопределения, он отвечает за кол-во отображаемых записей, в моем случае уже стоит __Int.MAX_VALUE__, чтобы отображать бесконечный список.

Нужно же как-то всё вывести на экран телефона, поэтому напишем нашу MainActivity:
__Листинг 2.4 - MainActivity.kt__

    package com.example.android_lab4

    import android.os.Bundle
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.DividerItemDecoration
    import androidx.recyclerview.widget.LinearLayoutManager
    import com.example.android_lab4.databinding.ActivityMainBinding


    class MainActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val binding = ActivityMainBinding.inflate(layoutInflater)

            val manager = LinearLayoutManager(this)

            binding.recyclerView.apply {
                addItemDecoration(DividerItemDecoration(context, manager.orientation))
                layoutManager = manager
                adapter = Adapter(resources.openRawResource(R.raw.articles))
            }

            setContentView(binding.root)
        }
    }
Разбираемся с отображением:
- также переопределяем метод __onCreate__, биндим наш основной Layout. Теперь нам нужно создать обычный вертикальный LinearLayout - вызываем __LinearLayoutManager__ с одним параметром для context'a.
- теперь вызываем метод __apply__ для извлеченного из .xml-файла recyclerView. Здесь необходимо добавить декорации для нашего recyclerView, за это отвечает метод __addItemDecoration__, который принимает на вход один параметр *ItemDecoration* (в нашем случае *DividerItemDecoration* с 2 параметрами: текущий context и ориентация - в нашем случае вертикальная, так как мы взяли стандартный LinearLayoutManager). После этого мы устанавливаем layoutManager и adapter, в качестве них выступают наши LinearLayoutManager и Adapter-класс соответственно.
- выводим всё на экран.

## Задача 3 - Бесконечный список.

Собственно, его мы уже сделали - за это отвечает метод __getItemCount()__, в который мы установили *Int.MAX_VALUE*

# Выводы
- Как я уже отметил в первом пункте отчета - расстроила Java, я никогда не привыкал к объявлению типа переменной и простановке ";", поэтому раз 5 раз падал из-за вот таких банальных ошибок. Но с первым пунктом полностью справился часа за 2.
- Благодаря лекциям, можно сказать, что 2 и 3 пункты выполнились примерно также за 2 часа.

Итого: 4 часа в AS и 4 часа составления красивого отчета и чтение документации по методам с лекций, чтобы понять, что мы отдаем и принимаем на вход.
И как мне кажется, когда записи хорошо структурированы, продемонстрированный в лабораторной работе метод хорошо справляется с поставленной задачей.
