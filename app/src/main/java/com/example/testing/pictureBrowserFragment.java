package com.example.testing;

import static androidx.core.view.ViewCompat.setTransitionName;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;


public class pictureBrowserFragment extends Fragment implements imageIndicatorListener {

    private  ArrayList<pictureFacer> allImages = new ArrayList<>();
    private int position;
    private Context animeContx;
    private PhotoView image;
    private Button back;
    ImageButton trsh,edtpbtn,share;
    TextView cancl,del;
    private ViewPager imagePager;
    private RecyclerView indicatorRecycler;
    private int viewVisibilityController;
    private int viewVisibilitylooper;
    private ImagesPagerAdapter pagingImages;
    private int previousSelected = -1;

    public pictureBrowserFragment()
    {
        
    }
    public pictureBrowserFragment(ArrayList<pictureFacer> allImages, int imagePosition, Context anim) {
        this.allImages = allImages;
        this.position = imagePosition;
        this.animeContx = anim;
    }

    public static pictureBrowserFragment newInstance(ArrayList<pictureFacer> allImages, int imagePosition, Context anim) {
        pictureBrowserFragment fragment = new pictureBrowserFragment(allImages,imagePosition,anim);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.picture_browser, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /**
         * initialisation of the recyclerView visibility control integers
         */
        viewVisibilityController = 0;
        viewVisibilitylooper = 0;

        /**
         * setting up the viewPager with images
         */
        imagePager = view.findViewById(R.id.imagePager);
        pagingImages = new ImagesPagerAdapter();
        imagePager.setAdapter(pagingImages);
        imagePager.setOffscreenPageLimit(3);
        imagePager.setCurrentItem(position);//displaying the image at the current position passed by the ImageDisplay Activity


        indicatorRecycler = view.findViewById(R.id.indicatorRecycler);
        indicatorRecycler.hasFixedSize();
        indicatorRecycler.setLayoutManager(new GridLayoutManager(getContext(),1,RecyclerView.HORIZONTAL,false));
        RecyclerView.Adapter indicatorAdapter = new recyclerViewPagerImageIndicator(allImages,getContext(),this);
        indicatorRecycler.setAdapter(indicatorAdapter);

        //adjusting the recyclerView indicator to the current position of the viewPager, also highlights the image in recyclerView with respect to the
        //viewPager's position
        allImages.get(position).setSelected(true);
        previousSelected = position;
        indicatorAdapter.notifyDataSetChanged();
        indicatorRecycler.scrollToPosition(position);


        /**
         * this listener controls the visibility of the recyclerView
         * indication and it current position in respect to the image ViewPager
         */
        imagePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if(previousSelected != -1){
                    allImages.get(previousSelected).setSelected(false);
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }else{
                    previousSelected = position;
                    allImages.get(position).setSelected(true);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    indicatorRecycler.scrollToPosition(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        indicatorRecycler.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**
                   uncomment the below condition to control recyclerView visibility automatically
                   when image is clicked also uncomment the condition set on the image's onClickListener in the ImagesPagerAdapter adapter
                 */
                /*if(viewVisibilityController == 0){
                    indicatorRecycler.setVisibility(View.VISIBLE);
                    visibiling();
                }else{
                    viewVisibilitylooper++;
                }*/
                return false;
            }
        });

    }

    @Override
    public void onImageIndicatorClicked(int ImagePosition)
    {
        //the below lines of code highlights the currently select image in  the indicatorRecycler with respect to the viewPager position
        if(previousSelected != -1){
            allImages.get(previousSelected).setSelected(false);
            previousSelected = ImagePosition;
            indicatorRecycler.getAdapter().notifyDataSetChanged();
        }else{
            previousSelected = ImagePosition;
        }

        imagePager.setCurrentItem(ImagePosition);
    }

    private class ImagesPagerAdapter extends PagerAdapter
    {
        @Override
        public int getCount() {
            return allImages.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup containerCollection, int position) {
            LayoutInflater layoutinflater = (LayoutInflater) containerCollection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutinflater.inflate(R.layout.picture_browser_pager,null);

              image = view.findViewById(R.id.imagevie);
              edtpbtn=view.findViewById(R.id.editbutton);
              back=view.findViewById(R.id.back);
              trsh=view.findViewById(R.id.trashimg);
            share=view.findViewById(R.id.shareimage);

            ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

            Dialog dialogimg = new Dialog(getActivity());
            dialogimg.setContentView(R.layout.deleteimagepopup);
            dialogimg.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialogimg.setCancelable(false);
            dialogimg.getWindow().getAttributes().windowAnimations= R.style.Theme_Testing;
            cancl=dialogimg.findViewById(R.id.canclimgbtn);
            del=dialogimg.findViewById(R.id.delimgbtn);

            setTransitionName(image, String.valueOf(position)+"picture");

            pictureFacer pic = allImages.get(position);
            Glide.with(animeContx)
                    .load(pic.getPicturePath())
                    .apply(new RequestOptions().fitCenter())
                    .into(image);

            trsh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogimg.show();
                }
            });

            cancl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogimg.hide();
                }
            });

            del.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    String pcpath = pic.getPicturePath();
                    File file = new File(pcpath);
                    deleteimgRecursive(file);
                    indicatorRecycler.getAdapter().notifyDataSetChanged();
                    imagePager.setCurrentItem(position+1);

                    dialogimg.hide();

                }
            });



            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(indicatorRecycler.getVisibility() == View.GONE){
                        indicatorRecycler.setVisibility(View.VISIBLE);
                    }else{
                        indicatorRecycler.setVisibility(View.GONE);
                    }
                    /**
                     * uncomment the below condition and comment the one above to control recyclerView visibility automatically
                     * when image is clicked
                     */
                    /*if(viewVisibilityController == 0){
                     indicatorRecycler.setVisibility(View.VISIBLE);
                     visibiling();
                 }else{
                     viewVisibilitylooper++;
                 }*/

                }
            });


back.setOnClickListener(new View.OnClickListener()
{
    public void onClick(View v)
    {
        //Refreshing Activity Without lossing data on Object
        getActivity().finish();
        startActivity(getActivity().getIntent());

    }
});
            edtpbtn.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), PictureEdit.class);
                    String uti =  Uri.fromFile(new File(pic.getPicturePath())).toString();
                    intent.putExtra("image",uti);
                    startActivity(intent);
                }

            });

            share.setOnClickListener(new View.OnClickListener()
            {
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), PictureEdit.class);
                    String pt=pic.getPicturePath();
                    Uri imgUri = Uri.parse(pt);
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp.w4b");
                    whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                    whatsappIntent.setType("image/jpeg");
                    whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    try {
                        animeContx.startActivity(whatsappIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(animeContx,"Whatsapp have not been installed.",Toast.LENGTH_LONG).show();

                    }
                }

            });


            ((ViewPager) containerCollection).addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup containerCollection, int position, Object view) {
            ((ViewPager) containerCollection).removeView((View) view);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == ((View) object);
        }
    }

    /**
     * function for controlling the visibility of the recyclerView indicator
     */
    private void visibiling(){
        viewVisibilityController = 1;
        final int checker = viewVisibilitylooper;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(viewVisibilitylooper > checker){
                   visibiling();
                }else{
                   indicatorRecycler.setVisibility(View.GONE);
                   viewVisibilityController = 0;

                   viewVisibilitylooper = 0;
                }
            }
        }, 4000);
    }

    void deleteimgRecursive(File fileOrDirectory) {
        if (fileOrDirectory.exists()) {
            if (fileOrDirectory.delete()) {
                System.out.println("file Deleted :" + fileOrDirectory.getPath());
            } else {
                System.out.println("file not Deleted :" + fileOrDirectory.getPath());
            }
        }

    }

}
