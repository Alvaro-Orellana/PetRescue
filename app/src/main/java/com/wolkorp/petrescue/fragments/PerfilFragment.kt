package com.wolkorp.petrescue.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.wolkorp.petrescue.R
import com.wolkorp.petrescue.models.User
import kotlinx.android.synthetic.main.fragment_perfil.*


class PerfilFragment : Fragment() {

    lateinit var fragmentView : View
    lateinit var nombre : TextView
    lateinit var pais : TextView
    lateinit var email : TextView
    lateinit var numero: TextView
    lateinit var profileImage : ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_perfil, container, false)
        nombre = fragmentView.findViewById(R.id.nombre)
        pais = fragmentView.findViewById(R.id.pais)
        email = fragmentView.findViewById(R.id.email)
        numero = fragmentView.findViewById(R.id.numero)
        profileImage = fragmentView.findViewById(R.id.profile_img)

        return fragmentView
    }


    override fun onStart() {
        super.onStart()
        loadUserData()

        btn_ver_posts_activos.setOnClickListener {
            it.findNavController().navigate(R.id.action_perfilFragment_to_misPostsFragment)
        }

        btn_editar.setOnClickListener {
            it.findNavController().navigate(R.id.action_perfilFragment_to_editarPerfilFragment)
        }

        logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun loadUserData() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        if(currentUserId != null) {
            val query =  FirebaseFirestore
                                            .getInstance()
                                            .collection("Users")
                                            .document(currentUserId)

            //query.get().addOnSuccessListener { document ->
                query.addSnapshotListener{ document,e ->
                if (document != null) {

                    val user: User = document.toObject()!!
                    nombre.text =user.userName
                    pais.text = user.pais
                    email.text = user.email
                    numero.text = user.phoneNumber
                    updateImage(user.profileImageUrl)
                    Toast.makeText(context, "Exito obteniendo el usuario", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(context, "No existe el usuario con id $currentUserId", Toast.LENGTH_LONG).show()
                }
            }


        }
    }


    private fun updateImage(link : String){
        Glide
            .with(fragmentView)
            .load(link)
            .into(profileImage)
    }


    // Limpia las shared preferences, sale de la cuenta
    private fun logOut() {
        val prefs = requireContext().getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        Firebase.auth.signOut()
        fragmentView.findNavController().navigate(R.id.action_perfilFragment_to_authActivity)
    }

}