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

public class ViajeActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    EditText etCodviaje, etDestino, etCantidad, etValor;
    CheckBox cbActivo;
    RequestQueue rq;
    JsonRequest jrq;
    String CodigoViaje, CiudadDestino, Cantidad, valor, url, codigo;
    byte sw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viaje);

        getSupportActionBar().hide();
        etCodviaje = findViewById(R.id.etCodviaje);
        etDestino = findViewById(R.id.etDestino);
        etCantidad = findViewById(R.id.etCantidad);
        etValor = findViewById(R.id.etValor);
        cbActivo = findViewById(R.id.cbActivo);
        sw = 0;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this, "Codigo no registrado", Toast.LENGTH_SHORT).show();

    }


    private void limpiarCampos() {
        etCodviaje.setText("");
        etDestino.setText("");
        etCantidad.setText("");
        etValor.setText("");
        cbActivo.setChecked(false);
        etCodviaje.requestFocus();
        sw = 0;
    }

    public void cancelar(View view) {
        limpiarCampos();
    }

    public void Guardar(View view) {
        CodigoViaje = etCodviaje.getText().toString();
        CiudadDestino = etDestino.getText().toString();
        Cantidad = etCantidad.getText().toString();
        valor = etValor.getText().toString();
        if (CodigoViaje.isEmpty() || CiudadDestino.isEmpty() || Cantidad.isEmpty() || valor.isEmpty()) {
            Toast.makeText(this, "todos los datos son requeridos", Toast.LENGTH_SHORT).show();
            etCodviaje.requestFocus();
        } else {
            if (sw == 0) {
                url = "http://172.16.60.31:8080/WebServices/viaje/registrocorreo.php";
            } else {
                url = "http://172.16.60.31:8080/WebServices/viaje/actualiza.php";
                sw = 0;
            }

            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            limpiarCampos();
                            Toast.makeText(getApplicationContext(), "codigo de destino realizado!", Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "codigo de destino incorrecto!", Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("CodigoViaje", etCodviaje.getText().toString().trim());
                    params.put("CiudadDestino", etDestino.getText().toString().trim());
                    params.put("Cantidad", etCantidad.getText().toString().trim());
                    params.put("valor", etValor.getText().toString().trim());
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);

        }


    }

    public void Consultar(View view) {
        codigo = etCodviaje.getText().toString();
        if (codigo.isEmpty()) {
            Toast.makeText(this, "codigo es requerido para la busqueda", Toast.LENGTH_SHORT).show();
            etCodviaje.requestFocus();
        } else {
            url = "http://172.16.60.31:8080/WebServices/viaje/consulta.php=" + codigo;
            jrq = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
            rq.add(jrq);
        }

    }

    @Override
    public void onResponse(JSONObject response) {
        sw = 1;
        Toast.makeText(this, "codigo registrado", Toast.LENGTH_SHORT).show();
        JSONArray jsonArray = response.optJSONArray("datos");
        JSONObject jsonObject = null;

        try {
            jsonObject = jsonArray.getJSONObject(0);// posicion o del arreglo
            etCodviaje.setText(jsonObject.optString("CodigoViaje"));
            etDestino.setText(jsonObject.optString("CiudadDestino"));
            etCantidad.setText(jsonObject.optString("Cantidad"));
            if (jsonObject.optString("activo").equals("si"))
                cbActivo.setChecked(true);
            else {
                cbActivo.setChecked(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void Regresar(View view) {
        Intent intentlogin = new Intent(this, MainActivity.class);
        startActivity(intentlogin);
    }

    public void Eliminar(View view) {
        codigo = etCodviaje.getText().toString();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "El codigo es requerido", Toast.LENGTH_SHORT).show();
            etCodviaje.requestFocus();
        } else {
            if (sw == 0) {
                url = "http://172.16.60.31:8080/WebServices/viaje/elimina.php";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                limpiarCampos();
                                Toast.makeText(getApplicationContext(), "Codigo  de destino eliminado", Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "codigo de destino incorrecto!", Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("codigo", etCodviaje.getText().toString().trim());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(postRequest);

            }


        }


    }

    public void Anular (View view) {
        codigo = etCodviaje.getText().toString();

        if (codigo.isEmpty()) {
            Toast.makeText(this, "El codigo de destino es requerido", Toast.LENGTH_SHORT).show();
            etCodviaje.requestFocus();
        } else {
            if (sw == 0) {
                url = "http://172.16.60.31:8080/WebServices/viaje/anula.php";
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                limpiarCampos();
                                Toast.makeText(getApplicationContext(), "codigo de destino  anulado", Toast.LENGTH_LONG).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "codigo de destino no anulado!", Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("codigo", etCodviaje.getText().toString().trim());
                        return params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(postRequest);

            }


        }
    }
}