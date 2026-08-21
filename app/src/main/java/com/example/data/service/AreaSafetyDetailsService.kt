package com.example.data.service

import com.example.data.model.AreaDetails

class AreaSafetyDetailsService {

    fun getAreaDetails(lat: Double, lng: Double, resolvedCity: String): AreaDetails {
        val cityLower = resolvedCity.lowercase()

        return when {
            "delhi" in cityLower || (lat in 28.3..28.9 && lng in 76.8..77.5) -> {
                AreaDetails(
                    localityName = "Central Delhi & Heritage Zone",
                    city = "New Delhi",
                    stateCountry = "Delhi NCR, India",
                    safetyScore = 91,
                    safetyRatingLabel = "High Tourist Police Surveillance Zone",
                    soloFemaleRating = 4.6,
                    nightSafetyAdvisory = "Main avenues (Kartavya Path, Connaught Place, Chanakyapuri) are well illuminated with 24/7 PCR patrols. Prefer Delhi Metro or app-cabs after 22:30.",
                    topScams = listOf(
                        "The 'Closed Monument' Claim: Auto drivers asserting Red Fort/Temple is shut for VIP visits to divert you to expensive commission handicraft showrooms.",
                        "Counterfeit Tour Guides: Unofficial guides without ASI photo IDs offering 'fast-track' entry at Humayun's Tomb and Qutub Minar.",
                        "Shoe Staining Trick: Pedestrians intentionally dropping black paste on your shoes to charge ₹500 for emergency polishing."
                    ),
                    localEtiquettes = listOf(
                        "Footwear must be deposited at free token counters prior to entering temple/mosque premises.",
                        "Dress respectfully with shoulders and knees covered when visiting spiritual shrines.",
                        "Metro women's coach is strictly reserved (first car in train direction)."
                    ),
                    transitTips = listOf(
                        "Delhi Metro is the fastest and safest transit mode. Buy a tourist day pass for unlimited rides.",
                        "For auto-rickshaws, insist on running by digital meter or use official Prepaid Booths outside major railway stations.",
                        "Verified SafeWheels & Uber/Ola pickup bays are marked with green signage at T3/T1 airports."
                    ),
                    nearestPoliceOutpost = "Delhi Tourist Police Assistance Booth • Connaught Place (180m away)",
                    nearestHospital = "All India Institute of Medical Sciences (AIIMS) • 24/7 Trauma Center"
                )
            }
            "jaipur" in cityLower || (lat in 26.7..27.2 && lng in 75.6..76.1) -> {
                AreaDetails(
                    localityName = "Walled Pink City & Fort Corridor",
                    city = "Jaipur",
                    stateCountry = "Rajasthan, India",
                    safetyScore = 93,
                    safetyRatingLabel = "Verified Heritage & Tourism District",
                    soloFemaleRating = 4.8,
                    nightSafetyAdvisory = "Bazaars (Johari & Bapu) are very bustling until 21:30. Night tours at Amber Fort have dedicated security escort.",
                    topScams = listOf(
                        "Gemstone & Export Scheme: Friendly merchants asking travelers to carry precious stones back to their home country for quick commission.",
                        "Fake Elephant Ride Bookings: Unlicensed touts selling non-ASI elephant vouchers outside Amber Fort gates.",
                        "Spurious Saffron & Spices: Scented colored grass sold as genuine Kashmiri saffron in roadside kiosks."
                    ),
                    localEtiquettes = listOf(
                        "Greet locals with 'Khamma Ghani' or 'Namaste' with hands folded in respect.",
                        "Always ask for permission before photographing local artisans and village elders.",
                        "Bargaining in traditional bazaars is customary; polite smile and offering 60% of first quoted price is standard."
                    ),
                    transitTips = listOf(
                        "Jaipur Metro connects Railway Station to Chandpole in the Old City.",
                        "Low-floor AC city buses run on fixed routes (Route 1 connects Hawa Mahal to Amber Fort).",
                        "RTDC (Rajasthan Tourism Development Corp) provides verified full-day heritage hop-on buses."
                    ),
                    nearestPoliceOutpost = "Rajasthan Tourist Police Thana • Hawa Mahal Complex (120m away)",
                    nearestHospital = "Sawai Man Singh (SMS) Multi-Specialty Hospital • JLN Marg"
                )
            }
            "agra" in cityLower || (lat in 27.1..27.3 && lng in 77.9..78.2) -> {
                AreaDetails(
                    localityName = "Taj Protected Heritage Zone",
                    city = "Agra",
                    stateCountry = "Uttar Pradesh, India",
                    safetyScore = 89,
                    safetyRatingLabel = "Strict Archaeological Security Enclave",
                    soloFemaleRating = 4.5,
                    nightSafetyAdvisory = "Taj Ganj neighborhood is peaceful; avoid unlit side alleys after 22:00 and use pre-booked hotel transit.",
                    topScams = listOf(
                        "Marble Inlay Imitation: Soapstone artifacts marketed as authentic translucent Makrana marble inlay.",
                        "Taj Viewpoint Roof Touts: Boys demanding entry money for public rooftop views overlooking Taj Mahal.",
                        "Battery Rickshaw Overcharge: Overcharging for the mandatory 500m electric vehicle transfer from ticket counter to monument gate."
                    ),
                    localEtiquettes = listOf(
                        "No eatables, lighters, or tripod stands are permitted inside the Taj Mahal security perimeter.",
                        "Shoe covers are provided free with high-value tourist tickets at the entrance turnstiles."
                    ),
                    transitTips = listOf(
                        "Only zero-emission battery vehicles are allowed within 500m of the Taj Mahal.",
                        "Use the official UP Tourism prepaid counter at Agra Cantt Railway Station."
                    ),
                    nearestPoliceOutpost = "UP Tourist Police Station • Taj Mahal East Gate (90m away)",
                    nearestHospital = "SN Medical College & Hospital • Hospital Road"
                )
            }
            "goa" in cityLower || (lat in 14.9..15.9 && lng in 73.5..74.4) -> {
                AreaDetails(
                    localityName = "Coastal Tourism & Beach Safety Belt",
                    city = "Goa",
                    stateCountry = "Goa, India",
                    safetyScore = 95,
                    safetyRatingLabel = "Very Safe Coastal Tourist Haven",
                    soloFemaleRating = 4.9,
                    nightSafetyAdvisory = "North & South beach shacks and night markets are lively until midnight. High patrol frequency along Calangute, Baga, and Anjuna.",
                    topScams = listOf(
                        "Scooter Damage Surcharge: Non-licensed bike rental shops demanding hefty repair fees for pre-existing scratches on return. (Always take 360° video during checkout).",
                        "Unregistered Watersport Operators: Operators lacking Drishti Marine safety licenses offering cheap jet-ski rides.",
                        "Counterfeit Sunburn / Party Passes: Street vendors selling fake VIP tickets for club events."
                    ),
                    localEtiquettes = listOf(
                        "Beachwear is appropriate on beaches and pools; please wear shirts/dresses when entering towns, churches, and shops.",
                        "Strict 'No Glass Bottles on Beach' regulation enforced with on-the-spot fines."
                    ),
                    transitTips = listOf(
                        "Rent scooters only from shops displaying yellow-on-black commercial number plates.",
                        "GoaMiles government app offers fixed transparent taxi fares.",
                        "Drishti Marine lifeguards are posted every 200m on all major beaches from 07:00 to 18:30."
                    ),
                    nearestPoliceOutpost = "Goa Tourist Police Coastal Unit • Calangute Beach Outpost (50m from shore)",
                    nearestHospital = "Goa Medical College (GMC) & Hospital • Bambolim"
                )
            }
            else -> {
                AreaDetails(
                    localityName = "Active City Center & Explorer District",
                    city = resolvedCity.ifBlank { "Metropolitan Area" },
                    stateCountry = "Explorer Protection Zone",
                    safetyScore = 92,
                    safetyRatingLabel = "Monitored Safe Tourist Corridor",
                    soloFemaleRating = 4.7,
                    nightSafetyAdvisory = "Active commercial streets are well-lit. Stick to verified transportation and keep emergency speed-dial active.",
                    topScams = listOf(
                        "Unofficial Transport Solicitations: Drivers approaching inside airport arrival halls. Head directly to official taxi kiosks.",
                        "Currency Exchange Margins: Unlicensed money changers charging hidden processing fees.",
                        "Distraction Techniques: People spilling liquids or pointing out dropped coins while an accomplice attempts pickpocketing."
                    ),
                    localEtiquettes = listOf(
                        "Always carry digital copies of identification and keep emergency guardian contacts updated in SafeYatra.",
                        "Ask before taking close-up portraits of local community members.",
                        "Use polite formal greetings when addressing storekeepers and transport staff."
                    ),
                    transitTips = listOf(
                        "Prefer public metro lines or app-based rides with GPS route tracking enabled.",
                        "Ensure your phone has offline emergency maps saved before entering remote scenic areas."
                    ),
                    nearestPoliceOutpost = "City Tourist Assistance & PCR Unit (Nearby)",
                    nearestHospital = "Regional District Emergency Care Center"
                )
            }
        }
    }
}
