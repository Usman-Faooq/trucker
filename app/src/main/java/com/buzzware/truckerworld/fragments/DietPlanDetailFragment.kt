package com.buzzware.truckerworld.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.buzzware.truckerworld.databinding.FragmentDietPlanDetailBinding


class DietPlanDetailFragment(var type : String) : Fragment() {

    lateinit var binding : FragmentDietPlanDetailBinding

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentDietPlanDetailBinding.inflate(layoutInflater)

        if (type == "1") {
            binding.dietDay1.text = "DAY 1: CHICKEN & PASTA WITH CHOICE OF SAUCE & SALAD"
            binding.dietDay2.text = "DAY 2: TURKEY  & CHILI WITH CORN BREAD MUFFINS"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: BEEF TACO WITH CHIPS & SALSA"
            binding.dietDay5.text = "DAY 5: SALMON WITH RICE & BROCCOLI"
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        } else if (type == "2") {
            binding.dietDay1.text = "DAY 1: CHICKEN WRAP & VEGGIES WITH CHOICE OF DRESSING"
            binding.dietDay2.text = "DAY 2: CHICKEN, BACON DEEP DISH(TURKEY BACON RECOMENDED) & SIDE SALAD"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: SAUSAGE & POTATOES SOAP (OR ANY CHOICE OF SOUP) & TOAST"
            binding.dietDay5.text = "DAY 5: LEMON CHICKEN WITH CHOICE OF DIPPING"
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        } else if (type == "3") {
            binding.dietDay1.text = "DAY 1: CHILI BOWL & CHICKEN TACOS"
            binding.dietDay2.text = "DAY 2: TURKEY BURGER & FRIES"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: CHICKEN BOWL WITH RICE & BROCCOLI"
            binding.dietDay5.text = "DAY 5: SPAGHETTI WITH CHOICE OF SAUCE & SIDE SALAD"
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        } else if (type == "4") {
            binding.dietDay1.text = "DAY 1: LENTIL SOAP & FLAT BREAD (OVEN BAKED RECOMENDED)"
            binding.dietDay2.text = "DAY 2: BONELESS RIBS(BEEF RECOMENDED) WITH CHIPS & SALSA"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: BOWL OF CHICKEN WITH RICE & BROCCOLI"
            binding.dietDay5.text = "DAY 5: PHILLY CHEESE STEAK WITH HOAGIES ROLLS"
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        } else if (type == "5") {
            binding.dietDay1.text = "DAY 1: CHICKEN ENCHILADAS WITH CHIPS & SALSA"
            binding.dietDay2.text = "DAY 2: TURKEY PASTA & SIDE SALAD"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: SAUSAGE SOAP(CHICKEN OR TURKEY RECOMENDED)"
            binding.dietDay5.text = "DAY 5: CHICKEN WITH PASTA & VEGGIES"
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        } else if (type == "6") {
            binding.dietDay1.text = "DAY 1: CHICKEN SANDWICH WITH VEGGIES"
            binding.dietDay2.text = "DAY 2: TURKEY BURGERS & FRIES"
            binding.dietDay3.text = "DAY 3: (DAY FAST) 6:30AM-4:30PM (LIGHT DINNER)"
            binding.dietDay4.text = "DAY 4: SLOPPY JOE & VEGGIES"
            binding.dietDay5.text = "DAY 5: BBQ SANDWICH & CHIPS "
            binding.dietDay6.text = "DAY 6: LEFT OVER FROM THE WEEK"
            binding.dietDay7.text = "DAY 7: CHEAT DAY (EAT WHAT EVERY YOU LIKE)"
        }


        return binding.root
    }
}