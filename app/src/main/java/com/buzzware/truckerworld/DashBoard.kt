package com.buzzware.truckerworld

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.buzzware.truckerworld.databinding.ActivityDashBoardBinding
import com.buzzware.truckerworld.fragments.*


class DashBoard : AppCompatActivity(), PersonalRouteFragment.OnDataChangeListener, DietPlanFragment.OnDataChangeListener {

    lateinit var binding : ActivityDashBoardBinding

    var mainName : String = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setView()
        setListener()
        setDrawerListener()
        loadFragment(HomeFragment())

    }

    private fun setDrawerListener() {

        binding.homeLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadHomeFragment()
        }

        binding.scheduleLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadScheduleFragment()
        }

        binding.messageLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadMessageFragment()
        }

        binding.personalRouteLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadPersonalRouteFragment()
        }

        binding.dietPlanLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadDietFragment()
        }

        binding.onlineMemberLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadOnlineMemberFragment()
        }

        binding.requestLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadRequestFragment()
        }

        binding.myProfileLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadProfileFragment()
        }

        binding.guidelinesLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(PrivacyPolicyFragment())
            binding.appbar.searchIV.visibility = View.INVISIBLE
            binding.appbar.notificationIV.visibility = View.INVISIBLE
            binding.appbar.menuIV.setTag(R.drawable.menu_icon)
            binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
            binding.appbar.titleTV.setText("Guidelines")
        }

        binding.privacyPolicyLayout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            loadFragment(PrivacyPolicyFragment())
            binding.appbar.searchIV.visibility = View.INVISIBLE
            binding.appbar.notificationIV.visibility = View.INVISIBLE
            binding.appbar.menuIV.setTag(R.drawable.menu_icon)
            binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
            binding.appbar.titleTV.setText("Privacy Policy")
        }

        binding.logoutLayout.setOnClickListener {
            finish()
        }

    }


    private fun setView() {
        binding.appbar.notificationIV.setImageResource(R.drawable.notification_icon)
        binding.appbar.notificationIV.setTag(R.drawable.notification_icon)

        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

                val mainContentView = binding.mainContent

                val drawerWidth = drawerView.width.toFloat()
                val moveFactor = drawerWidth * slideOffset

                mainContentView.translationX = moveFactor

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {

            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })

    }

    private fun setListener() {

        binding.appbar.searchIV.setOnClickListener {
            binding.appbar.menuIV.setTag(R.drawable.back_icon)
            binding.appbar.menuIV.setImageResource(R.drawable.back_icon)
            binding.appbar.searchIV.visibility = View.INVISIBLE
            binding.appbar.notificationIV.visibility = View.INVISIBLE
            binding.appbar.titleTV.setText("Search")
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.container, SearchhFragment())
            transaction.addToBackStack(null);
            transaction.commit()

        }

        binding.appbar.menuIV.setOnClickListener {
            if (binding.appbar.menuIV.getTag() == R.drawable.back_icon){
                binding.appbar.menuIV.setTag(R.drawable.menu_icon)
                binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
                binding.appbar.searchIV.visibility = View.VISIBLE
                binding.appbar.notificationIV.visibility = View.VISIBLE
                binding.appbar.titleTV.setText(mainName)
                onBackPressed()
            } else if (binding.appbar.menuIV.getTag() == R.drawable.back_icon4){
                binding.appbar.menuIV.setTag(R.drawable.menu_icon)
                binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
                binding.appbar.notificationIV.visibility = View.INVISIBLE
                binding.appbar.titleTV.setText(mainName)
                onBackPressed()
            }else if(binding.appbar.menuIV.getTag() == R.drawable.back_icon2) {
                binding.appbar.menuIV.setTag(R.drawable.menu_icon)
                binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
                binding.appbar.notificationIV.visibility = View.VISIBLE
                binding.appbar.notificationIV.setImageResource(R.drawable.add_icon)
                binding.appbar.notificationIV.setTag(R.drawable.add_icon)
                binding.appbar.titleTV.setText(mainName)
                onBackPressed()
            }else if(binding.appbar.menuIV.getTag() == R.drawable.back_icon3) {
                binding.appbar.menuIV.setTag(R.drawable.back_icon2)
                binding.appbar.menuIV.setImageResource(R.drawable.back_icon2)
                binding.appbar.notificationIV.visibility = View.VISIBLE
                binding.appbar.notificationIV.setImageResource(R.drawable.add_user_icon)
                binding.appbar.notificationIV.setTag(R.drawable.add_user_icon)
                binding.appbar.titleTV.setText(mainName)
                onBackPressed()
            }else {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }

        binding.appbar.notificationIV.setOnClickListener {
            if (binding.appbar.notificationIV.tag == R.drawable.add_icon){
                binding.appbar.menuIV.tag = R.drawable.back_icon2
                binding.appbar.menuIV.setImageResource(R.drawable.back_icon2)
                binding.appbar.notificationIV.visibility = View.INVISIBLE
                binding.appbar.titleTV.text = "Create Group"
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, CreateGroupFragment())
                transaction.addToBackStack(null);
                transaction.commit()

            }else if (binding.appbar.notificationIV.tag == R.drawable.notification_icon){
                binding.appbar.menuIV.tag = R.drawable.back_icon
                binding.appbar.menuIV.setImageResource(R.drawable.back_icon)
                binding.appbar.searchIV.visibility = View.INVISIBLE
                binding.appbar.notificationIV.visibility = View.INVISIBLE
                binding.appbar.titleTV.text = "Notifications"
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, NotificationFragment())
                transaction.addToBackStack(null);
                transaction.commit()

                //Toast.makeText(this, "Notification Clicked...", Toast.LENGTH_SHORT).show()
            }else if (binding.appbar.notificationIV.tag == R.drawable.add_user_icon){

                binding.appbar.menuIV.tag = R.drawable.back_icon3
                binding.appbar.menuIV.setImageResource(R.drawable.back_icon3)
                binding.appbar.notificationIV.visibility = View.INVISIBLE
                binding.appbar.titleTV.text = "Add Group Members"
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, AddGroupMemberFragment())
                transaction.addToBackStack(null);
                transaction.commit()
            }else{
                binding.appbar.menuIV.setTag(R.drawable.back_icon)
                binding.appbar.menuIV.setImageResource(R.drawable.back_icon)
                binding.appbar.notificationIV.visibility = View.INVISIBLE
                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, EditProfileFragment())
                transaction.addToBackStack(null);
                transaction.commit()

            }
        }

        binding.homeTab.setOnClickListener {
            loadHomeFragment()
        }

        binding.scheduleTab.setOnClickListener {
            loadScheduleFragment()
        }

        binding.addTab.setOnClickListener {

            loadFragment(CreateScheduleFragment())
            binding.appbar.notificationIV.setImageResource(R.drawable.notification_icon)
            binding.appbar.notificationIV.setTag(R.drawable.notification_icon)
            binding.appbar.notificationIV.visibility = View.VISIBLE
            binding.appbar.searchIV.visibility = View.INVISIBLE
            binding.appbar.menuIV.setTag(R.drawable.menu_icon)
            binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
            binding.appbar.titleTV.setText("Create Schedule")
            binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
            binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
            binding.addTab.setImageResource(R.drawable.nav_add_selected)
            binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
            binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

        }

        binding.messageTab.setOnClickListener {
            loadMessageFragment()
        }

        binding.profileTab.setOnClickListener {
            loadProfileFragment()

        }

    }

    private fun loadHomeFragment() {
        mainName = "Home"
        loadFragment(HomeFragment())
        binding.appbar.notificationIV.setImageResource(R.drawable.notification_icon)
        binding.appbar.notificationIV.setTag(R.drawable.notification_icon)
        binding.appbar.notificationIV.visibility = View.VISIBLE
        binding.appbar.searchIV.visibility = View.VISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_selected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadScheduleFragment() {
        mainName = "My Schedule"
        loadFragment(ScheduleFragment())
        binding.appbar.notificationIV.setImageResource(R.drawable.notification_icon)
        binding.appbar.notificationIV.setTag(R.drawable.notification_icon)
        binding.appbar.notificationIV.visibility = View.VISIBLE
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_selected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadMessageFragment() {
        mainName = "Message"
        loadFragment(MessageFragment())
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.notificationIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_selected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadProfileFragment() {
        mainName = "Profile"
        loadFragment(ProfileFragment())
        binding.appbar.notificationIV.setImageResource(R.drawable.write_new_message)
        binding.appbar.notificationIV.setTag(R.drawable.write_new_message)
        binding.appbar.notificationIV.visibility = View.VISIBLE
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_selected)

    }

    private fun loadPersonalRouteFragment() {
        mainName = "Personal Route"
        loadFragment(PersonalRouteFragment())
        binding.appbar.notificationIV.setImageResource(R.drawable.add_icon)
        binding.appbar.notificationIV.setTag(R.drawable.add_icon)
        binding.appbar.notificationIV.visibility = View.VISIBLE
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadDietFragment() {
        mainName = "Diet Plans"
        loadFragment(DietPlanFragment())
        binding.appbar.notificationIV.visibility = View.INVISIBLE
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText(mainName)
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadOnlineMemberFragment() {
        loadFragment(OnlineMemberFragment())
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.notificationIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText("Online Members")
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadRequestFragment() {
        loadFragment(RequestsFragment())
        binding.appbar.searchIV.visibility = View.INVISIBLE
        binding.appbar.notificationIV.visibility = View.INVISIBLE
        binding.appbar.menuIV.setTag(R.drawable.menu_icon)
        binding.appbar.menuIV.setImageResource(R.drawable.menu_icon)
        binding.appbar.titleTV.setText("Requests")
        binding.homeTab.setImageResource(R.drawable.nav_home_unselected)
        binding.scheduleTab.setImageResource(R.drawable.nav_schedule_unselected)
        binding.addTab.setImageResource(R.drawable.nav_add_button_unselected)
        binding.messageTab.setImageResource(R.drawable.nav_message_unselected)
        binding.profileTab.setImageResource(R.drawable.nav_profile_unselected)

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)

        //transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun onDataChanged(data: String) {
        binding.appbar.menuIV.setTag(R.drawable.back_icon2)
        binding.appbar.menuIV.setImageResource(R.drawable.back_icon2)
        binding.appbar.notificationIV.setImageResource(R.drawable.add_user_icon)
        binding.appbar.notificationIV.setTag(R.drawable.add_user_icon)
        binding.appbar.titleTV.setText(data)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, GroupDetailFragment())
        transaction.addToBackStack(null);
        transaction.commit()
    }

    override fun onItemDataChanged(data: String, type: String) {
        binding.appbar.menuIV.setTag(R.drawable.back_icon4)
        binding.appbar.menuIV.setImageResource(R.drawable.back_icon4)
        binding.appbar.notificationIV.visibility = View.INVISIBLE
        binding.appbar.titleTV.setText(data)
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, DietPlanDetailFragment(type))
        transaction.addToBackStack(null);
        transaction.commit()
    }

}