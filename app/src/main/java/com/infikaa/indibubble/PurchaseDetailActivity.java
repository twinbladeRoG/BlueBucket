package com.infikaa.indibubble;

import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the

 * to handle interaction events.
 * Use the {@link PurchaseDetailActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PurchaseDetailActivity extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters


    public PurchaseDetailActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment PurchaseDetailActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static PurchaseDetailActivity newInstance() {
        PurchaseDetailActivity fragment = new PurchaseDetailActivity();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((IssueBackButton) getActivity()).issueBackButton(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_purchase_detail, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if( keyCode == KeyEvent.KEYCODE_BACK ) {

                    ((IssueBackButton) getActivity()).issueBackButton(true);

              /*      android.app.Fragment f=getFragmentManager().findFragmentByTag("PurchaseDetailActivity");
                    if(f != null && f instanceof Home) {

                        getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("Home")).replace(R.id.displaycontent,Home.newInstance("555444",((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()),"Home").addToBackStack("Home").commit();

                    }
*/

                 //   getFragmentManager().beginTransaction().replace(R.id.displaycontent,Home.newInstance("555444",((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth()),"Home").addToBackStack("Home").commit();
                getFragmentManager().popBackStackImmediate("ProductBill",FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    return true;
                } else {
                    return false;
                }
            }

        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


        ((IssueBackButton) getActivity()).issueBackButton(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    // TODO: Rename method, update argument and hook method into UI event


}
