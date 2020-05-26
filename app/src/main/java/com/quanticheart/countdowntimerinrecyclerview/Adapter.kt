package com.quanticheart.countdowntimerinrecyclerview

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_main.view.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class Adapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<Adapter.TimerHolder>() {
    private val dados = ArrayList<Dados>()

    init {
        dados.addAll(createData())
        recyclerView.apply {
            layoutManager =
                LinearLayoutManager(recyclerView.context, RecyclerView.VERTICAL, false)
            adapter = this@Adapter
        }
    }

    inner class TimerHolder(view: View) : RecyclerView.ViewHolder(view) {
        var timerCount: CountDownTimer? = null

        fun bind(dados: Dados) {
            itemView.titulo.text = dados.titulo
            itemView.inicial.text = dados.data
            timerCount?.let {
                it.cancel()
                startCounter(dados)
            } ?: run {
                startCounter(dados)
            }
        }

        private fun startCounter(dados: Dados) {
            val now = currentTime()
//            val future = removeFiveMinutes(convertCurrentTime(dados.data))
            val future = convertCurrentTime(dados.data)
            val diff = future - now
            val sec = TimeUnit.MILLISECONDS.toSeconds(diff)
            itemView.diff.text =
                if (sec.isPositive()) "Após a reciclagem da celula, tem $sec segundos para finalizar" else "Já finalizou"
            timerCount = object : CountDownTimer(sec * 1000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    itemView.contador.text = "" + millisUntilFinished / 1000 + " Sec"
                }

                override fun onFinish() {
                    itemView.contador.text = "Finalizou"
                }
            }.start()
        }
    }

    private fun Long.isPositive() = this > 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerHolder =
        TimerHolder(
            LayoutInflater.from(recyclerView.context).inflate(
                R.layout.item_main,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = dados.size

    override fun onBindViewHolder(TimerHolder: TimerHolder, position: Int) {
        TimerHolder.bind(dados[position])
    }

    private fun createData(): ArrayList<Dados> {
        val list = ArrayList<Dados>()
        val c = Calendar.getInstance().timeInMillis
        for (i in 0..59) {
            val date = getDate(c)
            val s = date.split(":")
            list.add(Dados("Contador - $i", "${s[0]}:${String.format("%02d", i)}:00"))
        }
        return list
    }

    private fun getDate(milliSeconds: Long): String { // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private val patter = "dd/MM/yyyy HH:mm:ss"
    @SuppressLint("SimpleDateFormat")
    private fun currentTime(): Long {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat(patter)
        val s = formatter.format(calendar.time)
        return formatter.parse(s).time
    }

    private fun convertCurrentTime(data: String): Long {
        val formatter = SimpleDateFormat(patter)
        return formatter.parse(data)?.time ?: 0
    }

    private fun removeFiveMinutes(dateTime: Long): Long {
        return Date(dateTime - 5 * 60000).time
    }
}

fun RecyclerView.createAdapter() {
    Adapter(this)
}
