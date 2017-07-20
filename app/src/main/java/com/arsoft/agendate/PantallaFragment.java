package com.arsoft.agendate;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arsoft.agendate.views.StaticHelpers;

/**
 * Created by Andrés on 1/24/2017.
 */
public class PantallaFragment extends Fragment {

    private Typeface mbBold;
    private Typeface mbRegular;
    private Typeface mbLight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //final ProgressDialog progress = ProgressDialog.show(getActivity(), "Procesando", "Por favor aguarde ...", true, false);
        final View returnView = inflater.inflate(R.layout.pago_salario_express_layout, container, false);

        final Button buttonMisContactos = (Button) returnView.findViewById(R.id.transferencia_vision_index_buttonMisContactos);
        final Button buttonMisGrupos = (Button) returnView.findViewById(R.id.transferencia_vision_index_buttonMisGrupos);
        final Button buttonPagarSalario = (Button) returnView.findViewById(R.id.transferencia_vision_index_buttonPagarSalario);

        final Typeface mbBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Bold.ttf");
        final Typeface mbRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/FSMillbank-Regular.ttf");

        for(TextView tw : StaticHelpers.getTextViewsByTag((ViewGroup) returnView, "bold")) {
            tw.setTypeface(mbBold);
        }

        for(TextView tw : StaticHelpers.getTextViewsByTag((ViewGroup) returnView, "regular")) {
            tw.setTypeface(mbRegular);
        }

        buttonMisContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticHelpers.showDialog(getActivity(), "buttonMisContactos");
            }
        });

        buttonMisGrupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticHelpers.showDialog(getActivity(), "buttonMisGrupos");
            }
        });

        buttonPagarSalario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StaticHelpers.showDialog(getActivity(), "buttonPagarSalario");
            }
        });

        return returnView;
    }
}
