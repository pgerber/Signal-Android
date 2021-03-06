package org.thoughtcrime.securesms.jobs;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.thoughtcrime.securesms.dependencies.InjectableType;
import org.thoughtcrime.securesms.jobmanager.JobParameters;
import org.thoughtcrime.securesms.jobmanager.SafeData;
import org.thoughtcrime.securesms.util.TextSecurePreferences;
import org.whispersystems.signalservice.api.SignalServiceAccountManager;
import org.whispersystems.signalservice.api.push.exceptions.PushNetworkException;

import java.io.IOException;

import javax.inject.Inject;

import androidx.work.Data;

@SuppressWarnings("WeakerAccess")
public class RotateCertificateJob extends ContextJob implements InjectableType {

  private static final long serialVersionUID = 1L;

  private static final String TAG = RotateCertificateJob.class.getName();

  @Inject transient SignalServiceAccountManager accountManager;

  public RotateCertificateJob() {
    super(null, null);
  }

  public RotateCertificateJob(Context context) {
    super(context, JobParameters.newBuilder()
                                .withGroupId("__ROTATE_SENDER_CERTIFICATE__")
                                .withNetworkRequirement()
                                .create());
  }

  @NonNull
  @Override
  protected Data serialize(@NonNull Data.Builder dataBuilder) {
    return dataBuilder.build();
  }

  @Override
  protected void initialize(@NonNull SafeData data) {

  }

  @Override
  public void onAdded() {}


  @Override
  public void onRun() throws IOException {
    byte[] certificate = accountManager.getSenderCertificate();
    TextSecurePreferences.setUnidentifiedAccessCertificate(context, certificate);
  }

  @Override
  public boolean onShouldRetry(Exception e) {
    return e instanceof PushNetworkException;
  }

  @Override
  public void onCanceled() {
    Log.w(TAG, "Failed to rotate sender certificate!");
  }
}
