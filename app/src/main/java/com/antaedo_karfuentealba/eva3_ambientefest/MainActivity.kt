package com.antaedo_karfuentealba.eva3_ambientefest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.antaedo_karfuentealba.eva3_ambientefest.data.local.SessionDataStore
import com.antaedo_karfuentealba.eva3_ambientefest.data.repository.Graph
import com.antaedo_karfuentealba.eva3_ambientefest.navigation.AppNavGraph
import com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.AuthViewModel
import com.antaedo_karfuentealba.eva3_ambientefest.ui.theme.Eva3_AmbienteFestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilitar edge-to-edge para mejor experiencia visual
        enableEdgeToEdge()

        // Inicializa el contenedor de dependencias
        Graph.provide(this)

        setContent {
            Eva3_AmbienteFestTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModel.Factory(Graph.authRepository)
                )

                // Crear CartViewModel localmente y pasarlo al NavGraph
                val cartViewModel: com.antaedo_karfuentealba.eva3_ambientefest.viewmodel.CartViewModel = viewModel()

                // Observa la sesión y sincroniza el carrito automáticamente: si no hay sesión limpiar; si hay, cargar carrito del userId
                val context = LocalContext.current
                val sessionStore = remember { SessionDataStore(context) }

                // Attach the session flow so CartViewModel reacts automatically to session changes
                // This will clear the cart on logout and load the cart when a new user logs in
                cartViewModel.attachSessionFlow(sessionStore.sessionFlow)

                // Llama correctamente a AppNavGraph con cartViewModel para limpiar estado en logout
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    cartViewModel = cartViewModel
                )
             }
         }
     }
 }
