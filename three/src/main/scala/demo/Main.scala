package demo

import org.scalablytyped.runtime.TopLevel
import org.scalajs.dom.UIEvent
import org.scalajs.dom.raw.HTMLDivElement
import typings.std.{FrameRequestCallback, Window_, document, requestAnimationFrame, stdStrings, window}
import typings.three.loaderMod.Loader
import typings.three.mod.{Math => ThreeMath, _}

import scala.scalajs.js
import scala.scalajs.js.Date
import scala.scalajs.js.annotation.JSImport

object Main {
  val radius = 600

  def main(argv: Array[String]): Unit = {

    val container = document.createElement_div(stdStrings.div)
    document.body.appendChild(container)

    val info: HTMLDivElement = document.createElement_div(stdStrings.div)
    info.style.position  = "absolute"
    info.style.top       = "10px"
    info.style.width     = "100%"
    info.style.textAlign = "center"
    info.innerHTML =
      """<a href="http://threejs.org" target="_blank" rel="noopener">three.js</a> webgl - morph targets - horse. model by <a href="http://mirada.com/">mirada</a> from <a href="http://ro.me">rome</a>"""
    container.appendChild(info)

    val camera = new PerspectiveCamera(50, window.innerWidth / window.innerHeight, 1, 10000)
    camera.position.y = 300
    val target = new Vector3(0, 150, 0)

    val scene = new Scene()
    scene.background = new Color(0xf0f0f0)

    val light1 = new DirectionalLight(0xefefff, 1.5)
    light1.position.set(1, 1, 1).normalize()
    scene.add(light1)

    val light2 = new DirectionalLight(0xffefef, 1.5)
    light2.position.set(-1, -1, -1).normalize()
    scene.add(light2)

    var mixerOpt: js.UndefOr[AnimationMixer] = js.undefined

    new GLTFLoader().load(
      HorseModel,
      gltf => {
        val mesh = gltf.scene.children(0)
        mesh.scale.set(1.5, 1.5, 1.5)
        scene.add(mesh)
        val mixer = new AnimationMixer(mesh)
        mixer.clipAction(gltf.animations(0)).setDuration(1).play()
        mixerOpt = mixer
      }
    )

    val renderer = new WebGLRenderer()
    renderer.setPixelRatio(window.devicePixelRatio)
    renderer.setSize(window.innerWidth, window.innerHeight)
    container.appendChild(renderer.domElement)

    val onWindowResize: js.ThisFunction1[Window_, UIEvent, Unit] = (window, _) => {
      camera.aspect = window.innerWidth / window.innerHeight
      camera.updateProjectionMatrix()
      renderer.setSize(window.innerWidth, window.innerHeight)
    }

    window.addEventListener_resize(stdStrings.resize, onWindowResize, false)

    var prevTime = Date.now()
    var theta    = 0.0

    def animate: FrameRequestCallback = time => {
      theta += 0.1

      camera.position.x = radius * Math.sin(ThreeMath.degToRad(theta))
      camera.position.z = radius * Math.cos(ThreeMath.degToRad(theta))

      camera.lookAt(target)

      mixerOpt.foreach { mixer =>
        mixer.update((time - prevTime) * 0.001)
        prevTime = time;
      }

      renderer.render(scene, camera)
      requestAnimationFrame(animate)
    }

    animate(0)
  }
}

/* Somewhat awkward that a bunch of the needed code live in `examples/`, which we don't currently convert */
@JSImport("three/examples/jsm/loaders/GLTFLoader", "GLTFLoader")
@js.native
class GLTFLoader() extends Loader {
  def load(url: String, onLoad: js.Function1[GLTF, Unit]): Unit = js.native
}

trait GLTF extends js.Object {
  val animations: js.Array[AnimationClip]
  val scene:      Scene
  val scenes:     js.Array[Scene]
  val cameras:    js.Array[Camera]
  val asset:      js.Object
}

@JSImport("../../../../src/main/resources/Horse.glb", JSImport.Namespace)
@js.native
object HorseModel extends TopLevel[String]
