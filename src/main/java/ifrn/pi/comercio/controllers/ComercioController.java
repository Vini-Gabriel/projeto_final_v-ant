package ifrn.pi.comercio.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import ifrn.pi.comercio.models.Produto;
import ifrn.pi.comercio.models.Venda;
import ifrn.pi.comercio.repositories.ProdutoRepository;
import ifrn.pi.comercio.repositories.VendaRepository;

@Controller
@RequestMapping("/comercio")
public class ComercioController {
	
	@Autowired
	private VendaRepository vr;
	@Autowired
	private ProdutoRepository pr;

	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/form")
	public String form(Venda venda) {
		return "comercio/formVenda";
	}
	
	//@PreAuthorize("hasRole('ROLE_ADMIN')")
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PostMapping
	public String salvar(@Valid Venda venda, BindingResult result, RedirectAttributes attributes) {
		
		if(result.hasErrors()) {
			return form(venda);
		}
		
		System.out.println(venda);
		vr.save(venda);
		attributes.addFlashAttribute("mensagem", "Venda efetuada com sucesso!");
		
		return "redirect:/comercio";
	}
	
//	@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping
	public ModelAndView listar() {
		List<Venda> vendas = vr.findAll();
		ModelAndView mv = new ModelAndView("comercio/lista");
		mv.addObject("vendas", vendas);
		return mv;
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{id}")
	public ModelAndView detalhar(@PathVariable Long id, Produto produto) {
		ModelAndView md = new ModelAndView();
		Optional<Venda> opt = vr.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		md.setViewName("comercio/detalhes");
		Venda venda = opt.get();
		md.addObject("venda", venda);
		
		List<Produto> produtos = pr.findByVenda(venda);
		md.addObject("produtos", produtos);
		
		return md;
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@PostMapping("/{idVenda}")
	public String salvarProduto(@PathVariable Long idVenda, Produto produto) {
		
		System.out.println("Id da venda: " + idVenda);
		System.out.println(produto);
		
		Optional<Venda> opt = vr.findById(idVenda);
		if(opt.isEmpty()) {
			return "redirect:/comercio";
		}
		
		Venda venda = opt.get();
		produto.setVenda(venda);
		
		pr.save(produto);
		
		return "redirect:/comercio/{idVenda}";
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{id}/selecionar")
	public ModelAndView selecionarVenda(@PathVariable Long id) {
		ModelAndView md = new ModelAndView(); 
		Optional<Venda> opt = vr.findById(id);
		if(opt.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		Venda venda = opt.get();
		md.setViewName("comercio/formVenda");
		md.addObject("venda", venda);
		
		return md;
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{idVenda}/produtos/{idProduto}/selecionar")
	public ModelAndView selecionarProduto(@PathVariable Long idVenda, @PathVariable Long idProduto) {
		ModelAndView md = new ModelAndView();
		
		Optional<Venda> optVenda = vr.findById(idVenda);
		Optional<Produto> optProduto = pr.findById(idProduto);
		
		if(optVenda.isEmpty() || optProduto.isEmpty()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		Venda venda = optVenda.get();
		Produto produto = optProduto.get();
		
		if(venda.getId() != produto.getVenda().getId()) {
			md.setViewName("redirect:/comercio");
			return md;
		}
		
		md.setViewName("comercio/detalhes");
		md.addObject("produto", produto);
		md.addObject("venda", venda);
		md.addObject("produtos", pr.findByVenda(venda));
		
		return md;
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{id}/remover")
	public String apagarVenda(@PathVariable Long id, RedirectAttributes attributes) {
		
		Optional<Venda> opt = vr.findById(id);
		
		if(!opt.isEmpty()) {
			Venda venda = opt.get();
			
			List<Produto> produtos = pr.findByVenda(venda);
			
			pr.deleteAll(produtos);
			vr.delete(venda);
			attributes.addFlashAttribute("mensagem", "Venda removido com sucesso!");
		}
		return "redirect:/comercio";
	}
	
	//@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/{idVenda}/produtos/{idProduto}/remover")
	public String apagarProduto(@PathVariable Long idVenda, @PathVariable Long idProduto) {
		
		Optional<Produto> opt = pr.findById(idProduto);
		
		if(!opt.isEmpty()) {
			Produto produto = opt.get();
			pr.delete(produto);
		}
		return "redirect:/comercio/{idVenda}";
	}

}
