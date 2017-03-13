/*===========================================================================

                        EDIT HISTORY FOR MODULE

This section contains comments describing changes made to the module.
Notice that changes are listed in reverse chronological order.

when      who            what, where, why
--------  ------         ------------------------------------------------------
20101012  Sang Mingxin   Initial to show the camera preview.

20120628  li.xujie@byd.com   modify it for EG808T Camera diag cmd test .

20120726  li.xujie@byd.com add it for camera diag test ( screen flicker  )

===========================================================================*/


#undef LOG_TAG
#define LOG_TAG "FT_CAMERA_SCREAN"

#include <utils/Log.h>

#include "ftscreen.h"
#include <pixelflinger/pixelflinger.h>
#include <unistd.h>

#include <linux/fb.h>
#include <linux/msm_mdp.h>
#include "pmem.h"

#define ANDROID_FTR
//#define X86_FTR

#define DUMP_PREVIEW_FRAMES
#undef DUMP_PREVIEW_FRAMES
#define BYD_FTSCREEN_TRACE  ALOGE


#define FTSCREEN_BIT_COUNT 2

#define FRAME_WIDTH     640
#define FRAME_HEIGHT    480

#define  FT_SCREEN_HEIGHT    800
#define  FT_SCREEN_WIDTH     480


/**************************/

int gh_fb = 0;
struct fb_var_screeninfo vinfo;
struct fb_fix_screeninfo finfo;
static GGLSurface gr_framebuffer[3];
void *bits;


/**************************/


long int crv_tab[256];
long int cbu_tab[256];
long int cgu_tab[256];

long int cgv_tab[256];
long int tab_76309[256];
unsigned char clp[1024];

void init_dither_tab()
{
    long int crv, cbu, cgu, cgv;
    int i, ind;

    crv = 104597; cbu = 132201; 
    cgu = 25675; cgv = 53279;

    for (i = 0; i < 256; i++) 
    {
        crv_tab[i] = (i-128) * crv;
        cbu_tab[i] = (i-128) * cbu;
        cgu_tab[i] = (i-128) * cgu;
        cgv_tab[i] = (i-128) * cgv;
        tab_76309[i] = 76309*(i-16);
    }

    for (i=0; i<384; i++)
    {
        clp[i] =0;
    }
    
    ind = 384;
    
    for (i=0; i<256; i++)
    {
        clp[ind++]=i;
    }
    
    ind = 640;
    
    for (i=0; i<384; i++)
    {
        clp[ind++] = 255;
    }
}

  void     YUV2RGB565(unsigned   char   *src,  __u16 *dst_ori, 
    int   width,int   height) 
  { 
  unsigned     char    *src0; 
  unsigned     char    *src1; 
  unsigned     char    *src2; 
  int    y1,y2,u,v;     
  unsigned     char    *py1,*py2; 
  int    i,j,   c1,     c2,   c3,     c4; 
  __u16 *d1,   *d2,   *d3; 

  char r,g,b;
  
  //Initialization 
  src0=src; 
  src1=src+width*height; 
  src2=src+width*height+width*height/4; 
  
  py1=src0; 
  py2=py1+width; 
  d1=dst_ori; 
  d2=d1+2*width; 
    for   (j   =   0;    j    <    height;   j   +=   2)    {    
  for    (i     =     0;   i   <   width;   i   +=    2)     { 
  
  u   =   *src1++; 
 // v   =   *src2++; 
 v   =   *src1++; 
  
  c1   =   crv_tab[v]; 
  c2   =   cgu_tab[u]; 
  c3   =   cgv_tab[v]; 
  c4   =   cbu_tab[u]; 
  
  //up-left 
   y1    =    tab_76309[*py1++]; 
  b   =   clp[384+((y1     +     c1)>> 16)];     
  g   =   clp[384+((y1     -     c2   -   c3)>> 16)]; 
   r   =   clp[384+((y1   +   c4)>> 16)]; 
  *d1++ = (((__u16)r>>3)<<11) | (((__u16)g>>2)<<5) | (((__u16)b>>3)<<0);
  //down-left 
  y2   =   tab_76309[*py2++]; 
  b   =   clp[384+((y2     +     c1)>> 16)];     
  g   =   clp[384+((y2     -     c2   -   c3)>> 16)]; 
  r   =   clp[384+((y2     +     c4)>> 16)]; 
  *d2++ = (((__u16)r>>3)<<11) | (((__u16)g>>2)<<5) | (((__u16)b>>3)<<0);
  //up-right 
  y1   =   tab_76309[*py1++]; 
  b   =   clp[384+((y1     +     c1)>> 16)];     
  g   =   clp[384+((y1     -     c2   -   c3)>> 16)]; 
  r   =   clp[384+((y1     +     c4)>> 16)]; 
  *d1++ = (((__u16)r>>3)<<11) | (((__u16)g>>2)<<5) | (((__u16)b>>3)<<0);
  //down-right 
  y2   =   tab_76309[*py2++]; 
  b   =   clp[384+((y2     +     c1)>> 16)];     
  g   =   clp[384+((y2     -     c2   -   c3)>> 16)]; 
  r   =   clp[384+((y2     +     c4)>> 16)]; 
  *d2++ = (((__u16)r>>3)<<11) | (((__u16)g>>2)<<5) | (((__u16)b>>3)<<0);
  } 
  d1   +=    2*width; 
  d2   +=    2*width; 
  py1+=       width; 
  py2+=       width; 
  }    


} 
int ftscreen_fb_open()
{
    int xres = 0;
    int yres = 0;
    int bits_per_pixel = 0;

    BYD_FTSCREEN_TRACE("\n ftscreen_fb_open()  Start  \n");
    
    gh_fb = open("/dev/graphics/fb0", O_RDWR);
    
    if (gh_fb < 0)
    {
        BYD_FTSCREEN_TRACE(" %s() : L(%d)  , Error: cannot open framebuffer device.\n",__func__,__LINE__);
        return -1;
    }

    if (ioctl(gh_fb, FBIOGET_FSCREENINFO, &finfo))
    {
        BYD_FTSCREEN_TRACE(" %s() : L(%d) , Error reading fixed information.\n",__func__,__LINE__);
        return -1;
    }

    if (ioctl(gh_fb, FBIOGET_VSCREENINFO, &vinfo))
    {
        BYD_FTSCREEN_TRACE(" %s() : L(%d) , Error reading variable information.\n",__func__,__LINE__);
        return -1;
    }

    bits = mmap(0, finfo.smem_len, PROT_READ | PROT_WRITE, MAP_SHARED, gh_fb, 0);
    if(bits == MAP_FAILED) {
       
      BYD_FTSCREEN_TRACE(" %s() : L(%d) , failed to mmap framebuffer.\n",__func__,__LINE__);
       return -1;
    }else{
    
         BYD_FTSCREEN_TRACE(" %s() : L(%d) , sucessful mmap framebuffer.\n",__func__,__LINE__);

   }
    
    gr_framebuffer[0].version = sizeof(*gr_framebuffer);
    gr_framebuffer[0].width = vinfo.xres;
    gr_framebuffer[0].height = vinfo.yres;
    gr_framebuffer[0].stride = finfo.line_length / (vinfo.bits_per_pixel >> 3);
    gr_framebuffer[0].data = (GGLubyte*)bits;
    gr_framebuffer[0].format = GGL_PIXEL_FORMAT_RGB_565;
    memset(gr_framebuffer[0].data, 0, vinfo.yres * vinfo.xres * vinfo.bits_per_pixel / 8);


    gr_framebuffer[1].version = sizeof(*gr_framebuffer);
    gr_framebuffer[1].width = vinfo.xres;
    gr_framebuffer[1].height = vinfo.yres;
    gr_framebuffer[1].stride = finfo.line_length / (vinfo.bits_per_pixel >> 3);
    gr_framebuffer[1].data = (GGLubyte*)(((unsigned) bits) + (vinfo.yres * vinfo.xres * vinfo.bits_per_pixel / 8));
    gr_framebuffer[1].format = GGL_PIXEL_FORMAT_RGB_565;
    memset(gr_framebuffer[1].data, 0, vinfo.yres * vinfo.xres * vinfo.bits_per_pixel / 8);

    gr_framebuffer[2].version = sizeof(*gr_framebuffer);
    gr_framebuffer[2].width = vinfo.xres;
    gr_framebuffer[2].height = vinfo.yres;
    gr_framebuffer[2].stride = finfo.line_length / (vinfo.bits_per_pixel >> 3);
    gr_framebuffer[2].data = (GGLubyte*)(((unsigned) bits) + 2 * (vinfo.yres * vinfo.xres * vinfo.bits_per_pixel / 8));
    gr_framebuffer[2].format = GGL_PIXEL_FORMAT_RGB_565;
    memset(gr_framebuffer[2].data, 0, vinfo.yres * vinfo.xres * vinfo.bits_per_pixel / 8);

///////////////////////////////////////////////////////////////////////////////////
    return 0;
}

void ftscreen_fb_close()
{

    BYD_FTSCREEN_TRACE("\n ftscreen_fb_close()  Start  \n");
    munmap(bits, finfo.smem_len);
    close(gh_fb);
    
}


int ftscreen_init()
{

     BYD_FTSCREEN_TRACE("\n ftscreen_init()  Start  \n");
     
    if (-1 == ftscreen_fb_open())
    {
    
        BYD_FTSCREEN_TRACE("\n %s() : L( %d )   ftscreen_fb_open error !!! \n",__func__,__LINE__);    
        return -1;
    }

    init_dither_tab();

    BYD_FTSCREEN_TRACE("[ftscreen_init] End\n\n");
    
    return 1;
    
}
/////////////////////////////////////////////////////////////////////
void set_active_framebuffer(unsigned n)
{

    BYD_FTSCREEN_TRACE("\n set_active_framebuffer()  Start  \n");
    
    if(n > 1) return;
    vinfo.yres_virtual = vinfo.yres*3 ;
    vinfo.yoffset = n * vinfo.yres;
    if(ioctl(gh_fb, FBIOPUT_VSCREENINFO, &vinfo) < 0) {

     BYD_FTSCREEN_TRACE("\n %s() : L( %d )  <zq> set_active_framebuffer error !!! \n",__func__,__LINE__);    
  
    } else{

     BYD_FTSCREEN_TRACE("\n %s() : L( %d )  <zq> set_active_framebuffer success  !!! \n",__func__,__LINE__);    
        
    }
    
}

int blit_op(unsigned char* yuv_buffer)
{
    struct mdp_blit_req* blitReq;
    
    union {
        
        char dummy[sizeof(struct mdp_blit_req_list) + sizeof(struct mdp_blit_req)*1];
        struct mdp_blit_req_list list;
        
    } image;
    
    BYD_FTSCREEN_TRACE("\n blit_op() start ; \n");
     
    initPmem();
    
    memset(PMEM->pmem_buf, 0, FRAME_WIDTH * FRAME_HEIGHT * 3 / 2);
    memcpy(PMEM->pmem_buf, yuv_buffer, FRAME_WIDTH * FRAME_HEIGHT * 3 / 2);
    image.list.count = 1;
    
    blitReq = &(image.list.req[0]);
    
    blitReq->src.width = FRAME_WIDTH;
    blitReq->src.height = FRAME_HEIGHT;
    blitReq->src.format = MDP_Y_CBCR_H2V2;
    blitReq->src.memory_id = PMEM->pmem_fd;
    blitReq->src.offset = 0;
    blitReq->src_rect.x = 0;
    blitReq->src_rect.y = 0;
    blitReq->src_rect.w = FRAME_WIDTH;
    blitReq->src_rect.h = FRAME_HEIGHT;
    
    blitReq->dst.width  = FT_SCREEN_WIDTH;
    blitReq->dst.height = FT_SCREEN_HEIGHT;
    blitReq->dst.format = MDP_FB_FORMAT;
    blitReq->dst.offset = 0;
    blitReq->dst.memory_id = gh_fb;
    blitReq->dst_rect.x = 0;
    blitReq->dst_rect.y = 0;
    blitReq->dst_rect.w = FT_SCREEN_WIDTH;    
    blitReq->dst_rect.h = FT_SCREEN_HEIGHT;   
    
    blitReq->transp_mask = MDP_TRANSP_NOP;
    blitReq->flags = MDP_DITHER;
    blitReq->flags |= MDP_ROT_90;
    blitReq->flags |= MDP_MEMORY_ID_TYPE_FB;
    blitReq->alpha = MDP_ALPHA_NOP;


    if(ioctl(gh_fb, MSMFB_BLIT, &(image.list)))
    {
    
          BYD_FTSCREEN_TRACE("\n  %s() : L(%d) , ioctl FBDIAG_MSMFB_BLIT failed !!!!! \n",__func__,__LINE__);
        return -1;
    }

    return 0;
    
}



int ftscreen_write(unsigned char* yuv_buffer)
{

#ifdef DUMP_PREVIEW_FRAMES
        static int frameCnt = 0;
        int written;
                if (frameCnt >= 0 && frameCnt <= 2 ) {
                    char buf[128];
                    sprintf(buf, "/data/%d_preview_zq.yuv", frameCnt);
                    int file_fd = open(buf, O_RDWR | O_CREAT, 0777);
                    ALOGV("dumping preview frame %d", frameCnt);
                    if (file_fd < 0) {
                        ALOGE("cannot open file\n");
                    }
                    else
                    {
                        ALOGV("dumping data");
                        written = write(file_fd, (uint8_t *)yuv_buffer,
                            320 * 480 * 3/2);
                        if(written < 0)
                          ALOGE("error in data write");
                    }
                    close(file_fd);
              }
              frameCnt++;
#endif

  BYD_FTSCREEN_TRACE("\n ftscreen_write()  Start  \n");

    //YUV2RGB565(yuv_buffer,(__u16 *)gr_framebuffer[0].data,FTSCREEN_WIDTH,FTSCREEN_HEIGHT);

  blit_op(yuv_buffer);

  set_active_framebuffer(0);
    
  ioctl(gh_fb, FBIOPAN_DISPLAY, &vinfo);
  
  return 0;

}

int ftscreen_destroy(void)
{
    
    BYD_FTSCREEN_TRACE("\n ftscreen_destroy()  Start  \n");
    
    ftscreen_fb_close();
    
    return 1;
    
}

    


