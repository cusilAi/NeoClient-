package net.kdt.pojavlaunch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

public class StarfieldView extends View {
    private static final int N = 90;
    private final Paint bg = new Paint();
    private final Paint star = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nebA = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint nebB = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] sx = new float[N];
    private final float[] sy = new float[N];
    private final float[] sr = new float[N];
    private final float[] sv = new float[N];
    private final float[] sp = new float[N];
    private float w, h, t;
    private long last;

    public StarfieldView(Context c) { super(c); }
    public StarfieldView(Context c, AttributeSet a) { super(c, a); }
    public StarfieldView(Context c, AttributeSet a, int s) { super(c, a, s); }

    @Override
    protected void onSizeChanged(int nw, int nh, int ow, int oh) {
        w = nw; h = nh;
        bg.setShader(new LinearGradient(0, 0, 0, h, new int[]{0xFF050814, 0xFF0B1230, 0xFF1A0F3D}, null, Shader.TileMode.CLAMP));
        float rad = Math.max(w, h) * 0.6f;
        nebA.setShader(new RadialGradient(w * 0.25f, h * 0.3f, rad, 0x557C4DFF, 0x007C4DFF, Shader.TileMode.CLAMP));
        nebB.setShader(new RadialGradient(w * 0.8f, h * 0.75f, rad, 0x4400B8D4, 0x0000B8D4, Shader.TileMode.CLAMP));
        Random r = new Random(7);
        for (int i = 0; i < N; i++) {
            sx[i] = r.nextFloat() * w;
            sy[i] = r.nextFloat() * h;
            sr[i] = 0.8f + r.nextFloat() * 2.2f;
            sv[i] = 4f + r.nextFloat() * 14f;
            sp[i] = r.nextFloat() * 6.28f;
        }
    }

    @Override
    protected void onDraw(Canvas cv) {
        long now = System.nanoTime();
        float dt = last == 0 ? 0.016f : Math.min((now - last) / 1e9f, 0.05f);
        last = now;
        t += dt;
        cv.drawRect(0, 0, w, h, bg);
        float dx = (float) Math.sin(t * 0.15f) * w * 0.06f;
        float dy = (float) Math.cos(t * 0.12f) * h * 0.04f;
        float rad = Math.max(w, h) * 0.6f;
        cv.save();
        cv.translate(dx, dy);
        cv.drawCircle(w * 0.25f, h * 0.3f, rad, nebA);
        cv.restore();
        cv.save();
        cv.translate(-dx, -dy);
        cv.drawCircle(w * 0.8f, h * 0.75f, rad, nebB);
        cv.restore();
        for (int i = 0; i < N; i++) {
            sy[i] += sv[i] * dt;
            if (sy[i] > h + 4) { sy[i] = -4; }
            float a = 0.35f + 0.65f * (0.5f + 0.5f * (float) Math.sin(t * 1.6f + sp[i]));
            star.setColor(0xFFFFFFFF);
            star.setAlpha((int) (a * 255));
            cv.drawCircle(sx[i], sy[i], sr[i], star);
        }
        postInvalidateOnAnimation();
    }
}
