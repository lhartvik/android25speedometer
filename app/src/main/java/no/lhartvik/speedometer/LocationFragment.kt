package no.lhartvik.speedometer

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import no.lhartvik.speedometer.databinding.FragmentLocationBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequest: LocationRequest? = null

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    binding.tekst1.text =
                        "${location?.speed?.let {  (3.6 * it).roundToInt().toString().padStart(2, '0')}}"
                    binding.tekst2.text =
                        "${location?.let { SimpleDateFormat("HH:mm:ss").format(Date(it.time)) }}"
                    binding.tekst3.text = "${location?.latitude}, ${location?.longitude}, ${locationResult.locations.size}"
                }
            }
        }
        locationRequest = LocationRequest.create()?.apply {
            interval = 5000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        fusedLocationClient
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        super.onResume()
    }

    override fun onPause() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}