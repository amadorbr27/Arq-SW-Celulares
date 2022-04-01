package com.example.infodispositivo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import com.example.infodispositivo.databinding.ActivitySecondBinding;
import java.util.ArrayList;
import java.util.List;


public class SecondActivity extends AppCompatActivity {

    private ActivitySecondBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnIrBattery2.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        binding.btnIrWifi2.setOnClickListener(view -> {
            Intent intent = new Intent(this, WifiActivity.class);
            startActivity(intent);
        });

        binding.btnIrApp2.setOnClickListener(view -> {
            Intent intent = new Intent(this, ThirdActivity.class);
            startActivity(intent);
        });

        Intent intent = new Intent(this, MyIntentService.class);
        intent.putExtra("TELA", "Contatos");
        startService(intent);

        List<MyContact> contacts = ContactsHelper.getContacts(this);
//        binding.tvContato2.setText(contacts.get(1).getName());

        List<String> listaContatos = new ArrayList<>();

        for (MyContact contact : contacts) {
            listaContatos.add(contact.getName());
        }

        if (contacts.size() >= 2) {
            contacts.get(1);
        }

        binding.tvListaCotatos.setAdapter(new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listaContatos));

    }
}