package profile.manager.location.based.auto.profile.changer.utils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import profile.manager.location.based.auto.profile.changer.broadcasts.Admin;

public class AdminHelper {
    private DevicePolicyManager devicePolicyManager;
    private ComponentName compName;
    Context context;

    public AdminHelper(Context context) {
        this.context = context;
        devicePolicyManager = (DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName( context, Admin.class ) ;
    }

    public boolean isActive (/*View view*/) {
        return devicePolicyManager.isAdminActive(compName);

    }
    public void intentToAdmin()
    {
        if (!isActive())
        {
            Intent intent = new Intent(DevicePolicyManager. ACTION_ADD_DEVICE_ADMIN ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_DEVICE_ADMIN , compName ) ;
            intent.putExtra(DevicePolicyManager. EXTRA_ADD_EXPLANATION , "You should enable the app!" ) ;
            context.startActivity(intent /*, PermissionCodes.ADMIN_RESULT_ENABLE*/ ) ;
        }

    }
    public void lockPhone (/*View view*/) {
        devicePolicyManager .lockNow() ;
    }
}
