package com.example.empresa25;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UsuarioActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText jetusuario, jetnombre, jetcorreo, jetclave;
    CheckBox jcbactivo;
    RequestQueue rq;
    JsonRequest jrq;
    String usr, nombre, correo, clave, url;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        // ocultar la barra , asociar objetos java con objetos xml
        // iniciar la cola de consultar
        getSupportActionBar().hide();
        jetusuario = findViewById(R.id.Edusu);
        jetnombre = findViewById(R.id.ednombre);
        jetcorreo = findViewById(R.id.edcorreo2);
        jetclave = findViewById(R.id.jetclave);
        jcbactivo = findViewById(R.id.cbActivo);
        rq = Volley.newRequestQueue(this);//requerimiento Volley
        sw = 0;

    }

    public void Guardar(View view) {
        usr = jetusuario.getText().toString();
        nombre = jetnombre.getText().toString();
        correo = jetcorreo.getText().toString();
        clave = jetclave.getText().toString();
        if (usr.isEmpty() || nombre.isEmpty() || correo.isEmpty() || clave.isEmpty()) {
            Toast.makeText(this, "todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            jetusuario.requestFocus();
        } else {
            if (sw == 0) {
                url = "http://172.16.60.31:8080/WebServices/registrocorreo.php";
            } else {
                url = "http://172.16.60.31:8080/WebServices/actualiza.php";
                sw = 0;
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Limpiar_Campos();
                            Toast.makeText(getApplicationContext(), "Registro de usuario realizado!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Registro de usuario incorrecto!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("usr", jetusuario.getText().toString().trim());
                    params.put("nombre", jetnombre.getText().toString().trim());
                    params.put("correo", jetcorreo.getText().toString().trim());
                    params.put("clave", jetclave.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);

        }


    }

    public void Consultar(View view) {
        usr = jetusuario.getText().toString();
        if (usr.isEmpty()) {
            Toast.makeText(this, "usuario es requerido para la busqueda", Toast.LENGTH_SHORT).show();
            jetusuario.requestFocus();
        } else {
            url = "http://172.16.60.31:8080/WebServices/consulta.php?usr=" + usr;
            jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            rq.add(jrq);
        }
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "usuario no registrado", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        sw = 1;
        Toast.makeText(this, "usuario registrado", Toast.LENGTH_SHORT).show();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);// posicion o del arreglo
            jetnombre.setText(jsonObject.optString("nombre"));
            jetcorreo.setText(jsonObject.optString("correo"));
            jetclave.setText(jsonObject.optString("clave"));
            if (jsonObject.optString("activo").equals("si"))
                jcbactivo.setChecked(true);
            else {
                jcbactivo.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void Regresar(View view) {
        Intent intentlogin = new Intent(this, MainActivity.class);
        startActivity(intentlogin);
    }

    public void Limpiar(View view) {
        Limpiar_Campos();
    }

    private void Limpiar_Campos() {
        sw = 0;
        jetusuario.setText("");
        jetnombre.setText("");
        jetcorreo.setText("");
        jetclave.setText("");
        jcbactivo.setChecked(false);
        jetusuario.requestFocus();


    }

    public void Eliminar(View view) {
        usr = jetusuario.getText().toString();

        if (usr.isEmpty()) {
            Toast.makeText(this, "El usuario es requerido", Toast.LENGTH_SHORT).show();
            jetusuario.requestFocus();
        } else {
            if (sw == 0) {
                url = "http://172.16.60.31:8080/WebServices/elimina.php";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Limpiar_Campos();
                                Toast.makeText(getApplicationContext(), "Registro de usuario eliminado", Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Registro de usuario incorrecto!", Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("usr", jetusuario.getText().toString().trim());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(postRequest);

            }


        }
    }
}
